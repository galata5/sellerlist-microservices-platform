CREATE TABLE outbox_events (
	event_id VARCHAR(64) NOT NULL PRIMARY KEY,
	aggregate_type VARCHAR(64) NOT NULL,
	aggregate_id VARCHAR(64) NOT NULL,
	event_type VARCHAR(128) NOT NULL,
	routing_key VARCHAR(128) NOT NULL,
	payload LONGTEXT NOT NULL,
	status VARCHAR(16) NOT NULL,
	attempts INT NOT NULL DEFAULT 0,
	next_attempt_at TIMESTAMP NULL,
	published_at TIMESTAMP NULL,
	last_error VARCHAR(512) NULL,
	created_at TIMESTAMP DEFAULT LOCALTIMESTAMP NOT NULL,
	updated_at TIMESTAMP NULL
);

CREATE INDEX idx_outbox_events_status_next_attempt_at
	ON outbox_events(status, next_attempt_at, created_at);
