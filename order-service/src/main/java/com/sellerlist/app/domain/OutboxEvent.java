package com.sellerlist.app.domain;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "outbox_events")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class OutboxEvent extends AbstractMappedEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "event_id", nullable = false, updatable = false, length = 64)
	private String eventId;

	@Column(name = "aggregate_type", nullable = false, length = 64)
	private String aggregateType;

	@Column(name = "aggregate_id", nullable = false, length = 64)
	private String aggregateId;

	@Column(name = "event_type", nullable = false, length = 128)
	private String eventType;

	@Column(name = "routing_key", nullable = false, length = 128)
	private String routingKey;

	@Lob
	@Column(name = "payload", nullable = false, columnDefinition = "LONGTEXT")
	private String payload;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 16)
	private OutboxEventStatus status;

	@Column(name = "attempts", nullable = false)
	private Integer attempts;

	@Column(name = "next_attempt_at")
	private Instant nextAttemptAt;

	@Column(name = "published_at")
	private Instant publishedAt;

	@Column(name = "last_error", length = 512)
	private String lastError;
}
