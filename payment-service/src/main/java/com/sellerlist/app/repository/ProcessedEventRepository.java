package com.sellerlist.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellerlist.app.domain.ProcessedEvent;
import com.sellerlist.app.domain.ProcessedEventId;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, ProcessedEventId> {
}
