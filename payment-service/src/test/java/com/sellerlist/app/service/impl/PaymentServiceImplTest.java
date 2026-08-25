package com.sellerlist.app.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sellerlist.app.domain.Payment;
import com.sellerlist.app.domain.PaymentStatus;
import com.sellerlist.app.domain.ProcessedEvent;
import com.sellerlist.app.domain.ProcessedEventId;
import com.sellerlist.app.repository.PaymentRepository;
import com.sellerlist.app.repository.ProcessedEventRepository;
import com.sellerlist.platform.events.OrderCreatedEvent;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private ProcessedEventRepository processedEventRepository;

	@InjectMocks
	private PaymentServiceImpl paymentService;

	@Test
	void provisionForOrderCreatesPendingPaymentWhenMissing() {
		when(paymentRepository.findByOrderId(42)).thenReturn(Optional.empty());
		when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
			final Payment payment = invocation.getArgument(0, Payment.class);
			payment.setPaymentId(7);
			return payment;
		});

		final var payment = paymentService.provisionForOrder(42, 9);

		assertThat(payment.getPaymentId()).isEqualTo(7);
		assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.NOT_STARTED);
		assertThat(payment.getIsPayed()).isFalse();
		assertThat(payment.getUserId()).isEqualTo(9);
		assertThat(payment.getOrderDto()).isNotNull();
		assertThat(payment.getOrderDto().getOrderId()).isEqualTo(42);
	}

	@Test
	void provisionForOrderDoesNotCreateDuplicatePayments() {
		final Payment existingPayment = Payment.builder()
				.paymentId(8)
				.orderId(42)
				.isPayed(Boolean.FALSE)
				.paymentStatus(PaymentStatus.IN_PROGRESS)
				.build();
		when(paymentRepository.findByOrderId(42)).thenReturn(Optional.of(existingPayment));

		final var payment = paymentService.provisionForOrder(42, 9);

		assertThat(payment.getPaymentId()).isEqualTo(8);
		assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.IN_PROGRESS);
		verify(paymentRepository, never()).save(any(Payment.class));
	}

	@Test
	void handleOrderCreatedSkipsAlreadyProcessedEvents() {
		final OrderCreatedEvent event = new OrderCreatedEvent(
				"event-1",
				"order.created.v1",
				1,
				"trace-1",
				"42",
				1,
				LocalDateTime.now(),
				42,
				null,
				9,
				32.50,
				"checkout",
				LocalDateTime.now());
		when(processedEventRepository.existsById(new ProcessedEventId("event-1", "payment-listener"))).thenReturn(true);

		paymentService.handleOrderCreated(event, "payment-listener");

		verify(paymentRepository, never()).save(any(Payment.class));
		verify(processedEventRepository, never()).save(any(ProcessedEvent.class));
	}
}
