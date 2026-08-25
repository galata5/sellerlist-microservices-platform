package com.sellerlist.app.resource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.sellerlist.app.dto.PaymentDto;
import com.sellerlist.app.dto.response.collection.DtoCollectionResponse;
import com.sellerlist.app.exception.wrapper.PaymentNotFoundException;
import com.sellerlist.app.service.PaymentService;
import com.sellerlist.platform.security.InternalRequestHeaders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payments")
@Slf4j
@RequiredArgsConstructor
public class PaymentResource {
	
	private final PaymentService paymentService;
	private final TaskScheduler paymentStreamTaskScheduler;
	
	@GetMapping
	public ResponseEntity<DtoCollectionResponse<PaymentDto>> findAll(
			@RequestHeader(InternalRequestHeaders.AUTHENTICATED_USER_ID) final Integer authenticatedUserId) {
		log.info("*** PaymentDto List, controller; fetch all payments *");
		return ResponseEntity.ok(new DtoCollectionResponse<>(this.paymentService.findAll(authenticatedUserId)));
	}
	
	@GetMapping("/{paymentId}")
	public ResponseEntity<PaymentDto> findById(
			@RequestHeader(InternalRequestHeaders.AUTHENTICATED_USER_ID) final Integer authenticatedUserId,
			@PathVariable("paymentId") final Integer paymentId) {
		log.info("*** PaymentDto, resource; fetch payment by id *");
		return ResponseEntity.ok(this.paymentService.findById(authenticatedUserId, paymentId));
	}

	@GetMapping("/order/{orderId}")
	public ResponseEntity<PaymentDto> findByOrderId(
			@RequestHeader(InternalRequestHeaders.AUTHENTICATED_USER_ID) final Integer authenticatedUserId,
			@PathVariable("orderId") final Integer orderId) {
		log.info("*** PaymentDto, resource; fetch payment by order id *");
		return ResponseEntity.ok(this.paymentService.findByOrderId(authenticatedUserId, orderId));
	}

	@GetMapping(path = "/stream/order/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter streamByOrderId(
			@RequestHeader(InternalRequestHeaders.AUTHENTICATED_USER_ID) final Integer authenticatedUserId,
			@PathVariable("orderId") final Integer orderId) {
		log.info("*** SseEmitter, resource; stream payment by order id *");
		final SseEmitter emitter = new SseEmitter(Duration.ofSeconds(30).toMillis());
		final AtomicReference<String> lastFingerprint = new AtomicReference<>();
		final ScheduledFuture<?> streamTask = this.paymentStreamTaskScheduler.scheduleAtFixedRate(() -> {
			try {
				final PaymentDto paymentDto = this.paymentService.findByOrderId(authenticatedUserId, orderId);
				final String fingerprint = paymentDto.getPaymentId() + ":" + paymentDto.getPaymentStatus() + ":" + paymentDto.getIsPayed();
				if (!fingerprint.equals(lastFingerprint.get())) {
					lastFingerprint.set(fingerprint);
					emitter.send(SseEmitter.event().name("payment-status").data(paymentDto));
				}
			} catch (final PaymentNotFoundException ignored) {
				// Payment provisioning is asynchronous; keep streaming until the payment exists.
			} catch (final Exception exception) {
				emitter.completeWithError(exception);
			}
		}, Duration.ofSeconds(1));
		emitter.onCompletion(() -> streamTask.cancel(true));
		emitter.onTimeout(() -> {
			streamTask.cancel(true);
			emitter.complete();
		});
		emitter.onError(throwable -> streamTask.cancel(true));
		return emitter;
	}
	
	@PostMapping
	public ResponseEntity<PaymentDto> save() {
		throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Payments are provisioned asynchronously by the platform.");
	}
	
	@PutMapping
	public ResponseEntity<PaymentDto> update() {
		throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Payment state is managed by trusted internal workflows only.");
	}
	
	@DeleteMapping("/{paymentId}")
	public ResponseEntity<Boolean> deleteById() {
		throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Payment records cannot be deleted through the public API.");
	}
	
	
	
}





