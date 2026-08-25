CREATE TABLE processed_events (
	event_id VARCHAR(64) NOT NULL,
	consumer_name VARCHAR(64) NOT NULL,
	processed_at TIMESTAMP DEFAULT LOCALTIMESTAMP NOT NULL,
	PRIMARY KEY (event_id, consumer_name)
);
