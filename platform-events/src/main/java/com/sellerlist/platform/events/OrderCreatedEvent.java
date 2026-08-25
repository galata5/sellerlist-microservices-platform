package com.sellerlist.platform.events;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record OrderCreatedEvent(
		String eventId,
		String eventType,
		Integer eventVersion,
		String traceId,
		String aggregateId,
		Integer aggregateVersion,
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		LocalDateTime occurredAt,
		Integer orderId,
		Integer cartId,
		Integer userId,
		Double orderFee,
		String orderDescription,
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		LocalDateTime orderDate) implements Serializable {

	private static final long serialVersionUID = 1L;
}
