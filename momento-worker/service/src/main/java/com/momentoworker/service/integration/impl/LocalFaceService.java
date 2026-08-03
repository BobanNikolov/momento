package com.momentoworker.service.integration.impl;

import com.momentoworker.data.model.Event;
import com.momentoworker.data.model.Photo;
import com.momentoworker.service.dto.FaceMetadata;
import com.momentoworker.service.integration.FaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Profile("local")
@Slf4j
public class LocalFaceService implements FaceService {
    @Override
    public void ensureCollection(Event event) {
        LOGGER.info("Local FaceService: ensureCollection for event {}", event.getId());
    }

    @Override
    public List<FaceMetadata> indexFace(String collectionId, Photo photo, byte[] bytes) {
        LOGGER.info("Local FaceService: indexFace for photo {}", photo.getId());
        FaceMetadata metadata = FaceMetadata.builder()
                .faceId(UUID.randomUUID().toString())
                .externalImageId(photo.getId().toString())
                .confidence(99.9f)
                .boundingBox(FaceMetadata.BoundingBox.builder()
                        .width(0.1f)
                        .height(0.1f)
                        .left(0.45f)
                        .top(0.45f)
                        .build())
                .build();
        return Collections.singletonList(metadata);
    }

    @Override
    public void deleteCollection(String collectionId) {
        LOGGER.info("Local FaceService: deleteCollection {}", collectionId);
    }
}
