package com.momentoworker.data.repository;

import com.momentoworker.data.model.Event;
import com.momentoworker.data.model.EventPhotographer;
import com.momentoworker.data.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventPhotographerRepository extends JpaRepository<EventPhotographer, Long> {
    List<EventPhotographer> findByEvent(Event event);
    List<EventPhotographer> findByPhotographer(UserAccount photographer);
}
