package com.momento.data.repository;

import com.momento.data.model.Event;
import com.momento.data.model.GuestSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestSearchRepository extends JpaRepository<GuestSearch, Long> {
    List<GuestSearch> findByEvent(Event event);
}
