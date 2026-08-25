package com.sellerlist.app.dto;

import java.io.Serializable;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sellerlist.app.domain.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaymentDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer paymentId;
	@NotNull
	private Boolean isPayed;
	@NotNull
	private PaymentStatus paymentStatus;
	private Integer userId;
	
	@JsonProperty("order")
	@JsonInclude(Include.NON_NULL)
	@Valid
	private OrderDto orderDto;
	
}









