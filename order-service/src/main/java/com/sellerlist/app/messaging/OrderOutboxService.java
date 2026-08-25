package com.sellerlist.app.messaging;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sellerlist.app.domain.OutboxEvent;
import com.sellerlist.app.domain.OutboxEventStatus;
import com.sellerlist.app.dto.OrderDto;
import com.sellerlist.app.repository.OutboxEventRepository;
import com.sellerlist.platform.events.OrderCreatedEvent;
import com.sellerlist.platform.events.OrderEvents;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderOutboxService {

	private final OutboxEventRepository outboxEventRepository;
	private final ObjectMapper objectMapper;

	public OrderOutboxService(
			final OutboxEventRepository outboxEventRepository,
			final ObjectMapper ignoredObjectMapper) {
		this.outboxEventRepository = outboxEventRepository;
		final ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		this.objectMapper = mapper;
	}

	public void enqueueOrderCreated(final OrderDto orderDto) {
		final OrderCreatedEvent event = new OrderCreatedEvent(
				UUID.randomUUID().toString(),
				OrderEvents.CREATED_ROUTING_KEY,
				OrderEvents.VERSION,
				UUID.randomUUID().toString(),
				String.valueOf(orderDto.getOrderId()),
				1,
				LocalDateTime.now(),
				orderDto.getOrderId(),
				orderDto.getCartDto() != null ? orderDto.getCartDto().getCartId() : null,
				orderDto.getUserId(),
				orderDto.getOrderFee(),
				orderDto.getOrderDesc(),
				orderDto.getOrderDate());

		try {
			outboxEventRepository.save(OutboxEvent.builder()
					.eventId(event.eventId())
					.aggregateType("ORDER")
					.aggregateId(String.valueOf(orderDto.getOrderId()))
					.eventType(OrderEvents.CREATED_ROUTING_KEY)
					.routingKey(OrderEvents.CREATED_ROUTING_KEY)
					.payload(objectMapper.writeValueAsString(event))
					.status(OutboxEventStatus.PENDING)
					.attempts(0)
					.nextAttemptAt(Instant.now())
					.build());
		} catch (final JsonProcessingException exception) {
			log.error("Unable to serialize order created event for orderId={}", orderDto.getOrderId(), exception);
			throw new IllegalStateException("Unable to persist order outbox event", exception);
		}
	}
}
