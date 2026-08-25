package com.sellerlist.app.messaging;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sellerlist.app.service.PaymentService;
import com.sellerlist.platform.events.OrderCreatedEvent;
import com.sellerlist.platform.events.OrderEvents;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderCreatedEventListener {

	private static final String CONSUMER_NAME = "payment-order-created-listener";
	private static final long MAX_RETRY_CYCLES = 3L;

	private final PaymentService paymentService;
	private final RabbitTemplate rabbitTemplate;
	private final ObjectMapper objectMapper;

	public OrderCreatedEventListener(
			final PaymentService paymentService,
			final RabbitTemplate rabbitTemplate,
			final ObjectMapper ignoredObjectMapper) {
		this.paymentService = paymentService;
		this.rabbitTemplate = rabbitTemplate;
		final ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		this.objectMapper = mapper;
	}

	@RabbitListener(queues = OrderEvents.PAYMENT_QUEUE)
	public void onOrderCreated(final String payload, final Message message) {
		try {
			final OrderCreatedEvent event = objectMapper.readValue(payload, OrderCreatedEvent.class);
			log.info("*** OrderCreatedEvent, listener; provision payment for orderId={} eventId={} *", event.orderId(), event.eventId());
			paymentService.handleOrderCreated(event, CONSUMER_NAME);
		} catch (final Exception exception) {
			final String eventId = String.valueOf(message.getMessageProperties().getHeaders().get(OrderEvents.EVENT_ID_HEADER));
			if (retryCount(message) >= MAX_RETRY_CYCLES) {
				rabbitTemplate.convertAndSend(OrderEvents.DLQ_EXCHANGE, OrderEvents.PAYMENT_DLQ_ROUTING_KEY, payload);
				log.error("OrderCreatedEvent moved to DLQ for eventId={}", eventId, exception);
				return;
			}
			throw new AmqpRejectAndDontRequeueException("Retrying order created event", exception);
		}
	}

	@SuppressWarnings("unchecked")
	private long retryCount(final Message message) {
		final Object xDeathHeader = message.getMessageProperties().getHeaders().get("x-death");
		if (!(xDeathHeader instanceof List<?> xDeaths) || xDeaths.isEmpty()) {
			return 0L;
		}
		final Object firstDeath = xDeaths.get(0);
		if (!(firstDeath instanceof Map<?, ?> deathEntry)) {
			return 0L;
		}
		final Object count = deathEntry.get("count");
		return count instanceof Number number ? number.longValue() : 0L;
	}
}
