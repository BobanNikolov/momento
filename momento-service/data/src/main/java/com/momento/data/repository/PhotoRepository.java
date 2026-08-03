package com.momento.data.repository;

import com.momento.data.model.Event;
import com.momento.data.model.Photo;
import com.momento.data.model.enums.PhotoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByEvent(Event event);
    List<Photo> findByEventAndProcessingStatus(Event event, PhotoStatus status);
    List<Photo> findByEventIdAndProcessingStatus(Long eventId, PhotoStatus status);
    List<Photo> findByProcessingStatus(PhotoStatus status);
}
