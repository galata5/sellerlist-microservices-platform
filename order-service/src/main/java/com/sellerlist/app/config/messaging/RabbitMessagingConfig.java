package com.sellerlist.app.config.messaging;

import org.springframework.amqp.core.DirectExchange;
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
	MessageConverter rabbitMessageConverter(final ObjectMapper objectMapper) {
		return new Jackson2JsonMessageConverter(objectMapper);
	}
}
