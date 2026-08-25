package com.sellerlist.app.messaging;

import java.time.Instant;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sellerlist.app.domain.OutboxEvent;
import com.sellerlist.app.domain.OutboxEventStatus;
import com.sellerlist.app.repository.OutboxEventRepository;
import com.sellerlist.platform.events.OrderEvents;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderOutboxPublisher {

	private static final int MAX_PUBLISH_ATTEMPTS = 5;

	private final OutboxEventRepository outboxEventRepository;
	private final OrderOutboxClaimService orderOutboxClaimService;
	private final RabbitTemplate rabbitTemplate;

	@Scheduled(fixedDelayString = "${app.outbox.publisher-fixed-delay-ms:5000}")
	public void publishPending() {
		this.orderOutboxClaimService.claimNextBatch(Instant.now()).forEach(this::publish);
	}

	private void publish(final OutboxEvent outboxEvent) {
		try {
			rabbitTemplate.convertAndSend(
					OrderEvents.EXCHANGE,
					outboxEvent.getRoutingKey(),
					outboxEvent.getPayload(),
					message -> {
						message.getMessageProperties().setContentType("application/json");
						message.getMessageProperties().setHeader(OrderEvents.EVENT_ID_HEADER, outboxEvent.getEventId());
						message.getMessageProperties().getHeaders().putAll(Map.of(
								"eventType", outboxEvent.getEventType(),
								"eventVersion", OrderEvents.VERSION,
								"aggregateType", outboxEvent.getAggregateType(),
								"aggregateId", outboxEvent.getAggregateId()));
						return message;
					});
			outboxEvent.setStatus(OutboxEventStatus.PUBLISHED);
			outboxEvent.setPublishedAt(Instant.now());
			outboxEvent.setNextAttemptAt(null);
			outboxEvent.setLastError(null);
			outboxEventRepository.save(outboxEvent);
		} catch (final Exception exception) {
			final int nextAttempts = outboxEvent.getAttempts() + 1;
			outboxEvent.setAttempts(nextAttempts);
			outboxEvent.setStatus(OutboxEventStatus.FAILED);
			outboxEvent.setLastError(truncate(exception.getMessage()));
			outboxEvent.setNextAttemptAt(nextAttempts >= MAX_PUBLISH_ATTEMPTS
					? null
					: Instant.now().plusSeconds(backoffSeconds(nextAttempts)));
			outboxEventRepository.save(outboxEvent);
			if (nextAttempts >= MAX_PUBLISH_ATTEMPTS) {
				log.error("Order outbox event {} exhausted publish attempts", outboxEvent.getEventId(), exception);
			} else {
				log.warn("Order outbox event {} publish attempt {} failed", outboxEvent.getEventId(), nextAttempts, exception);
			}
		}
	}

	private long backoffSeconds(final int attempts) {
		return switch (attempts) {
			case 1 -> 10L;
			case 2 -> 30L;
			case 3 -> 60L;
			default -> 300L;
		};
	}

	private String truncate(final String message) {
		if (message == null) {
			return null;
		}
		return message.length() > 500 ? message.substring(0, 500) : message;
	}
}
