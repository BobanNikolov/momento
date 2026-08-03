package com.momento.service.integration.impl;

import com.momento.service.integration.FaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.SearchFacesByImageRequest;
import software.amazon.awssdk.services.rekognition.model.SearchFacesByImageResponse;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Profile("aws")
@RequiredArgsConstructor
public class RekognitionFaceService implements FaceService {

    private final RekognitionClient rekognitionClient;

    @Override
    public List<String> searchByImage(String collectionId, byte[] selfieBytes) {
        LOGGER.info("Searching faces in Rekognition collection: {}", collectionId);
        SearchFacesByImageRequest request = SearchFacesByImageRequest.builder()
                .collectionId(collectionId)
                .image(Image.builder().bytes(SdkBytes.fromByteArray(selfieBytes)).build())
                .maxFaces(10)
                .faceMatchThreshold(70f)
                .build();

        SearchFacesByImageResponse response = rekognitionClient.searchFacesByImage(request);
        List<String> matches = response.faceMatches().stream()
                .map(match -> match.face().externalImageId())
                .collect(Collectors.toList());
        LOGGER.info("Rekognition face search completed for collection ID: {}. Found {} matches.", collectionId, matches.size());
        return matches;
    }
}
