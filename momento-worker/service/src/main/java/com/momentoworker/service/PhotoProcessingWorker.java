package com.momentoworker.service;

import com.momentoworker.data.model.Photo;
import com.momentoworker.data.model.RekognitionFace;
import com.momentoworker.data.model.enums.PhotoStatus;
import com.momentoworker.data.repository.PhotoRepository;
import com.momentoworker.data.repository.RekognitionFaceRepository;
import com.momentoworker.service.dto.FaceMetadata;
import com.momentoworker.service.integration.FaceService;
import com.momentoworker.service.integration.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static com.momentoworker.data.model.enums.PhotoStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhotoProcessingWorker {

    private final PhotoRepository photoRepository;
    private final RekognitionFaceRepository rekognitionFaceRepository;
    private final StorageService storageService;
    private final FaceService faceService;

    @Value("${aws.rekognition.collection-id-prefix}")
    private String collectionIdPrefix;

    @Value("${worker.max-retries:5}")
    private int maxRetries;

    @Transactional
    public void processPhoto(Long photoId) {
        LOGGER.info("Processing photo: {}", photoId);
        
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found"));

        if (photo.getProcessingStatus() == PROCESSED) {
            LOGGER.info("Photo {} is already PROCESSED, skipping", photoId);
            return;
        }

        if (photo.getRetryCount() >= maxRetries) {
            LOGGER.warn("Photo {} has reached max retries ({}), skipping", photoId, maxRetries);
            return;
        }

        try {
            photo.setProcessingStatus(PROCESSING);
            photoRepository.saveAndFlush(photo);

            byte[] originalBytes = storageService.getBytes(photo.getOriginalS3Key());

            // Generate thumbnail
            byte[] thumbnailBytes = generateThumbnail(originalBytes);
            String thumbnailKey = photo.getOriginalS3Key().replace("originals/", "thumbnails/");
            storageService.putBytes(thumbnailKey, thumbnailBytes, "image/jpeg");
            photo.setThumbnailS3Key(thumbnailKey);

            // Index faces
            faceService.ensureCollection(photo.getEvent());
            String collectionId = collectionIdPrefix + photo.getEvent().getId();
            List<FaceMetadata> faces = faceService.indexFace(collectionId, photo, originalBytes);

            for (FaceMetadata faceMetadata : faces) {
                RekognitionFace rekognitionFace = new RekognitionFace();
                rekognitionFace.setEvent(photo.getEvent());
                rekognitionFace.setPhoto(photo);
                rekognitionFace.setFaceId(faceMetadata.getFaceId());
                rekognitionFace.setExternalImageId(faceMetadata.getExternalImageId());
                rekognitionFace.setConfidence(faceMetadata.getConfidence());
                rekognitionFace.setBoundingBox(faceMetadata.getBoundingBox().toString());
                rekognitionFaceRepository.save(rekognitionFace);
            }

            photo.setProcessingStatus(PROCESSED);
            photo.setLastError(null);
            photoRepository.save(photo);
            
            LOGGER.info("Successfully processed photo: {}, faces found: {}", photoId, faces.size());
        } catch (Exception e) {
            LOGGER.error("Failed to process photo: {}", photoId, e);
            photo.setProcessingStatus(FAILED);
            photo.setRetryCount(photo.getRetryCount() + 1);
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.length() > 255) {
                errorMessage = errorMessage.substring(0, 255);
            }
            photo.setLastError(errorMessage);
            photoRepository.save(photo);
            throw new RuntimeException(e); // Rethrow to let the listener handle SQS deletion/retry
        }
    }

    private byte[] generateThumbnail(byte[] originalBytes) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(new ByteArrayInputStream(originalBytes))
                .size(400, 400)
                .outputFormat("jpg")
                .toOutputStream(outputStream);
        return outputStream.toByteArray();
    }
}
