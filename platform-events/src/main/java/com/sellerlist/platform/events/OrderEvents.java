package com.sellerlist.platform.events;

public final class OrderEvents {

	public static final String EXCHANGE = "sellerlist.orders";
	public static final String RETRY_EXCHANGE = "sellerlist.orders.retry";
	public static final String DLQ_EXCHANGE = "sellerlist.orders.dlq";
	public static final String CREATED_ROUTING_KEY = "order.created.v1";
	public static final String PAYMENT_QUEUE = "sellerlist.payment.order-created.v1";
	public static final String PAYMENT_RETRY_QUEUE = "sellerlist.payment.order-created.retry";
	public static final String PAYMENT_DLQ = "sellerlist.payment.order-created.dlq";
	public static final String PAYMENT_RETRY_ROUTING_KEY = "payment.order-created.retry";
	public static final String PAYMENT_DLQ_ROUTING_KEY = "payment.order-created.dlq";
	public static final String EVENT_ID_HEADER = "x-event-id";
	public static final int VERSION = 1;

	private OrderEvents() {
	}
}
