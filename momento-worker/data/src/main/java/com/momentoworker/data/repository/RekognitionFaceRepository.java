package com.momentoworker.data.repository;

import com.momentoworker.data.model.Event;
import com.momentoworker.data.model.Photo;
import com.momentoworker.data.model.RekognitionFace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RekognitionFaceRepository extends JpaRepository<RekognitionFace, Long> {
    List<RekognitionFace> findByEvent(Event event);
    List<RekognitionFace> findByPhoto(Photo photo);
    Optional<RekognitionFace> findByFaceId(String faceId);
    void deleteByEvent(Event event);
}
