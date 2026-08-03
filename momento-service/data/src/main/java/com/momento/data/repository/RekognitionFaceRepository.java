package com.momento.data.repository;

import com.momento.data.model.Event;
import com.momento.data.model.Photo;
import com.momento.data.model.RekognitionFace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RekognitionFaceRepository extends JpaRepository<RekognitionFace, Long> {
    List<RekognitionFace> findByEvent(Event event);
    List<RekognitionFace> findByPhoto(Photo photo);
    Optional<RekognitionFace> findByFaceId(String faceId);
}
