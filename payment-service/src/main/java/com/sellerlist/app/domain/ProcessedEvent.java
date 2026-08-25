package com.sellerlist.app.domain;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "processed_events")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProcessedEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ProcessedEventId id;

	@Column(name = "processed_at", nullable = false)
	private Instant processedAt;
}
