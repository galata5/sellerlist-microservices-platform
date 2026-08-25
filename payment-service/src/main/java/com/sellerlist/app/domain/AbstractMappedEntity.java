package com.sellerlist.app.domain;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
abstract public class AbstractMappedEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@CreationTimestamp
	@JsonFormat(shape = Shape.STRING)
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;
	
	@UpdateTimestamp
	@JsonFormat(shape = Shape.STRING)
	@Column(name = "updated_at")
	private Instant updatedAt;
	
}








