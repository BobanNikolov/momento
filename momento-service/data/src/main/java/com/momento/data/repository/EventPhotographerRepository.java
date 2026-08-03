package com.momento.data.repository;

import com.momento.data.model.Event;
import com.momento.data.model.EventPhotographer;
import com.momento.data.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventPhotographerRepository extends JpaRepository<EventPhotographer, Long> {
    List<EventPhotographer> findByEvent(Event event);
    List<EventPhotographer> findByPhotographer(UserAccount photographer);
    boolean existsByEventAndPhotographer(Event event, UserAccount photographer);
    void deleteByEventAndPhotographer(Event event, UserAccount photographer);
}
