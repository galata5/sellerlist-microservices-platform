package com.sellerlist.app.messaging;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sellerlist.app.domain.OutboxEvent;
import com.sellerlist.app.domain.OutboxEventStatus;
import com.sellerlist.app.repository.OutboxEventRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderOutboxClaimService {

	private static final java.time.Duration CLAIM_LEASE = java.time.Duration.ofMinutes(2);
	private final OutboxEventRepository outboxEventRepository;

	@Transactional
	public List<OutboxEvent> claimNextBatch(final Instant now) {
		final List<OutboxEvent> candidates = this.outboxEventRepository.lockNextBatch(now);
		candidates.forEach(event -> {
			event.setStatus(OutboxEventStatus.PROCESSING);
			event.setLastError(null);
			event.setNextAttemptAt(now.plus(CLAIM_LEASE));
		});
		return this.outboxEventRepository.saveAll(candidates);
	}
}
