package com.momentoworker.service.integration.impl;

import com.momentoworker.data.model.Event;
import com.momentoworker.data.model.Photo;
import com.momentoworker.service.dto.FaceMetadata;
import com.momentoworker.service.integration.FaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Profile("aws")
@RequiredArgsConstructor
@Slf4j
public class RekognitionFaceService implements FaceService {

    private final RekognitionClient rekognitionClient;

    @Value("${aws.rekognition.collection-id-prefix}")
    private String collectionIdPrefix;

    @Override
    public void ensureCollection(Event event) {
        String collectionId = collectionIdPrefix + event.getId();
        try {
            rekognitionClient.createCollection(CreateCollectionRequest.builder().collectionId(collectionId).build());
            LOGGER.info("Created Rekognition collection: {}", collectionId);
        } catch (ResourceAlreadyExistsException e) {
            LOGGER.debug("Collection already exists: {}", collectionId);
        }
    }

    @Override
    public List<FaceMetadata> indexFace(String collectionId, Photo photo, byte[] bytes) {
        IndexFacesRequest request = IndexFacesRequest.builder()
                .collectionId(collectionId)
                .image(Image.builder().bytes(SdkBytes.fromByteArray(bytes)).build())
                .externalImageId(photo.getId().toString())
                .maxFaces(1)
                .qualityFilter(QualityFilter.AUTO)
                .detectionAttributes(Attribute.ALL)
                .build();

        IndexFacesResponse response = rekognitionClient.indexFaces(request);
        return response.faceRecords().stream()
                .map(record -> FaceMetadata.builder()
                        .faceId(record.face().faceId())
                        .externalImageId(record.face().externalImageId())
                        .confidence(record.face().confidence())
                        .boundingBox(FaceMetadata.BoundingBox.builder()
                                .width(record.face().boundingBox().width())
                                .height(record.face().boundingBox().height())
                                .left(record.face().boundingBox().left())
                                .top(record.face().boundingBox().top())
                                .build())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCollection(String collectionId) {
        try {
            rekognitionClient.deleteCollection(DeleteCollectionRequest.builder().collectionId(collectionId).build());
            LOGGER.info("Deleted Rekognition collection: {}", collectionId);
        } catch (ResourceNotFoundException e) {
            LOGGER.warn("Collection not found for deletion: {}", collectionId);
        }
    }
}
