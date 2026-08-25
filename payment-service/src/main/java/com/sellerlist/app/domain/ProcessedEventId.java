package com.sellerlist.app.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProcessedEventId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "event_id", nullable = false, length = 64)
	private String eventId;

	@Column(name = "consumer_name", nullable = false, length = 64)
	private String consumerName;
}
