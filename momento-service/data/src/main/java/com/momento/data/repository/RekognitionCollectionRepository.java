package com.momento.data.repository;

import com.momento.data.model.RekognitionCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RekognitionCollectionRepository extends JpaRepository<RekognitionCollection, Long> {
    Optional<RekognitionCollection> findByAwsCollectionId(String awsCollectionId);
}
