package com.sellerlist.app.helper;

import com.sellerlist.app.domain.Payment;
import com.sellerlist.app.dto.OrderDto;
import com.sellerlist.app.dto.PaymentDto;

public interface PaymentMappingHelper {
	
	public static PaymentDto map(final Payment payment) {
		return PaymentDto.builder()
				.paymentId(payment.getPaymentId())
				.isPayed(payment.getIsPayed())
				.paymentStatus(payment.getPaymentStatus())
				.userId(payment.getUserId())
				.orderDto(
						OrderDto.builder()
							.orderId(payment.getOrderId())
							.build())
				.build();
	}
	
	public static Payment map(final PaymentDto paymentDto) {
		return Payment.builder()
				.paymentId(paymentDto.getPaymentId())
				.orderId(paymentDto.getOrderDto() != null ? paymentDto.getOrderDto().getOrderId() : null)
				.userId(paymentDto.getUserId())
				.isPayed(paymentDto.getIsPayed())
				.paymentStatus(paymentDto.getPaymentStatus())
				.build();
	}
	
	
	
}









