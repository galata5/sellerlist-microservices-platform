package com.sellerlist.app.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.sellerlist.app.dto.PaymentDto;
import com.sellerlist.app.domain.PaymentStatus;
import com.sellerlist.app.domain.ProcessedEvent;
import com.sellerlist.app.domain.ProcessedEventId;
import com.sellerlist.app.exception.wrapper.PaymentNotFoundException;
import com.sellerlist.app.helper.PaymentMappingHelper;
import com.sellerlist.app.repository.PaymentRepository;
import com.sellerlist.app.repository.ProcessedEventRepository;
import com.sellerlist.app.service.PaymentService;
import com.sellerlist.platform.events.OrderCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	
	private final PaymentRepository paymentRepository;
	private final ProcessedEventRepository processedEventRepository;
	
	@Override
	public List<PaymentDto> findAll(final Integer authenticatedUserId) {
		log.info("*** PaymentDto List, service; fetch all payments *");
		return this.paymentRepository.findAllByUserId(authenticatedUserId)
				.stream()
					.map(PaymentMappingHelper::map)
					.distinct()
					.collect(Collectors.toUnmodifiableList());
	}
	
	@Override
	public PaymentDto findById(final Integer authenticatedUserId, final Integer paymentId) {
		log.info("*** PaymentDto, service; fetch payment by id *");
		return this.paymentRepository.findByPaymentIdAndUserId(paymentId, authenticatedUserId)
				.map(PaymentMappingHelper::map)
				.orElseThrow(() -> new PaymentNotFoundException(String.format("Payment with id: %d not found", paymentId)));
	}

	@Override
	public PaymentDto findByOrderId(final Integer authenticatedUserId, final Integer orderId) {
		log.info("*** PaymentDto, service; fetch payment by order id *");
		return this.paymentRepository.findByOrderIdAndUserId(orderId, authenticatedUserId)
				.map(PaymentMappingHelper::map)
				.orElseThrow(() -> new PaymentNotFoundException(String.format("Payment for order id: %d not found", orderId)));
	}
	
	@Override
	public PaymentDto save(final Integer authenticatedUserId, final PaymentDto paymentDto) {
		log.info("*** PaymentDto, service; save payment *");
		paymentDto.setUserId(authenticatedUserId);
		return PaymentMappingHelper.map(this.paymentRepository
				.save(PaymentMappingHelper.map(paymentDto)));
	}
	
	@Override
	public PaymentDto update(final Integer authenticatedUserId, final PaymentDto paymentDto) {
		log.info("*** PaymentDto, service; update payment *");
		final PaymentDto existingPayment = this.findById(authenticatedUserId, paymentDto.getPaymentId());
		paymentDto.setUserId(existingPayment.getUserId());
		paymentDto.setOrderDto(existingPayment.getOrderDto());
		return PaymentMappingHelper.map(this.paymentRepository.save(PaymentMappingHelper.map(paymentDto)));
	}

	@Override
	public PaymentDto provisionForOrder(final Integer orderId, final Integer userId) {
		log.info("*** PaymentDto, service; provision payment for order id *");
		return this.paymentRepository.findByOrderId(orderId)
				.map(PaymentMappingHelper::map)
				.orElseGet(() -> PaymentMappingHelper.map(this.paymentRepository.save(com.sellerlist.app.domain.Payment.builder()
						.orderId(orderId)
						.userId(userId)
						.isPayed(Boolean.FALSE)
						.paymentStatus(PaymentStatus.NOT_STARTED)
						.build())));
	}

	@Override
	public void handleOrderCreated(final OrderCreatedEvent event, final String consumerName) {
		final ProcessedEventId processedEventId = new ProcessedEventId(event.eventId(), consumerName);
		if (processedEventRepository.existsById(processedEventId)) {
			log.info("*** OrderCreatedEvent, service; skip duplicate order created event eventId={} *", event.eventId());
			return;
		}

		provisionForOrder(event.orderId(), event.userId());
		processedEventRepository.save(ProcessedEvent.builder()
				.id(processedEventId)
				.processedAt(Instant.now())
				.build());
	}
	
	@Override
	public void deleteById(final Integer authenticatedUserId, final Integer paymentId) {
		log.info("*** Void, service; delete payment by id *");
		this.paymentRepository.delete(PaymentMappingHelper.map(this.findById(authenticatedUserId, paymentId)));
	}
	
	
	
}




