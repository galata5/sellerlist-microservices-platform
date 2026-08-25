package com.sellerlist.app.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sellerlist.app.domain.OutboxEvent;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {

	@Query(value = """
			SELECT * FROM outbox_events
			WHERE status IN ('PENDING', 'FAILED', 'PROCESSING')
			  AND next_attempt_at IS NOT NULL
			  AND next_attempt_at <= :nextAttemptAt
			ORDER BY created_at ASC
			LIMIT 50
			FOR UPDATE SKIP LOCKED
			""", nativeQuery = true)
	List<OutboxEvent> lockNextBatch(@Param("nextAttemptAt") Instant nextAttemptAt);
}
