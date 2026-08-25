package com.sellerlist.app.config.messaging;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sellerlist.platform.events.OrderEvents;

@Configuration
public class RabbitMessagingConfig {

	@Bean
	DirectExchange orderExchange() {
		return new DirectExchange(OrderEvents.EXCHANGE, true, false);
	}

	@Bean
	Queue paymentOrderCreatedQueue() {
		final Map<String, Object> arguments = new HashMap<>();
		arguments.put("x-dead-letter-exchange", OrderEvents.RETRY_EXCHANGE);
		arguments.put("x-dead-letter-routing-key", OrderEvents.PAYMENT_RETRY_ROUTING_KEY);
		return new Queue(OrderEvents.PAYMENT_QUEUE, true, false, false, arguments);
	}

	@Bean
	Binding paymentOrderCreatedBinding(final Queue paymentOrderCreatedQueue, final DirectExchange orderExchange) {
		return BindingBuilder.bind(paymentOrderCreatedQueue)
				.to(orderExchange)
				.with(OrderEvents.CREATED_ROUTING_KEY);
	}

	@Bean
	DirectExchange orderRetryExchange() {
		return new DirectExchange(OrderEvents.RETRY_EXCHANGE, true, false);
	}

	@Bean
	DirectExchange orderDeadLetterExchange() {
		return new DirectExchange(OrderEvents.DLQ_EXCHANGE, true, false);
	}

	@Bean
	Queue paymentOrderCreatedRetryQueue() {
		final Map<String, Object> arguments = new HashMap<>();
		arguments.put("x-message-ttl", 15000);
		arguments.put("x-dead-letter-exchange", OrderEvents.EXCHANGE);
		arguments.put("x-dead-letter-routing-key", OrderEvents.CREATED_ROUTING_KEY);
		return new Queue(OrderEvents.PAYMENT_RETRY_QUEUE, true, false, false, arguments);
	}

	@Bean
	Binding paymentOrderCreatedRetryBinding(
			final Queue paymentOrderCreatedRetryQueue,
			final DirectExchange orderRetryExchange) {
		return BindingBuilder.bind(paymentOrderCreatedRetryQueue)
				.to(orderRetryExchange)
				.with(OrderEvents.PAYMENT_RETRY_ROUTING_KEY);
	}

	@Bean
	Queue paymentOrderCreatedDeadLetterQueue() {
		return new Queue(OrderEvents.PAYMENT_DLQ, true);
	}

	@Bean
	Binding paymentOrderCreatedDeadLetterBinding(
			final Queue paymentOrderCreatedDeadLetterQueue,
			final DirectExchange orderDeadLetterExchange) {
		return BindingBuilder.bind(paymentOrderCreatedDeadLetterQueue)
				.to(orderDeadLetterExchange)
				.with(OrderEvents.PAYMENT_DLQ_ROUTING_KEY);
	}

	@Bean
	MessageConverter rabbitMessageConverter(final ObjectMapper objectMapper) {
		return new Jackson2JsonMessageConverter(objectMapper);
	}
}
