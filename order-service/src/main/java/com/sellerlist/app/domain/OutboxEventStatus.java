package com.sellerlist.app.domain;

public enum OutboxEventStatus {

	PENDING,
	PROCESSING,
	PUBLISHED,
	FAILED
}
