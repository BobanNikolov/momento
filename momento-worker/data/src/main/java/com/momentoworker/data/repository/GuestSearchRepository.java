package com.momentoworker.data.repository;

import com.momentoworker.data.model.Event;
import com.momentoworker.data.model.GuestSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GuestSearchRepository extends JpaRepository<GuestSearch, Long> {
    List<GuestSearch> findByEvent(Event event);
    List<GuestSearch> findBySelfieDeletedAtIsNullAndCreatedAtBefore(LocalDateTime threshold);
    void deleteByEvent(Event event);
}
