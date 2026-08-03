package com.momentoworker.service;

import com.momentoworker.data.model.Event;
import com.momentoworker.data.model.GuestSearch;
import com.momentoworker.data.model.enums.EventStatus;
import com.momentoworker.data.repository.*;
import com.momentoworker.service.integration.FaceService;
import com.momentoworker.service.integration.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.momentoworker.data.model.enums.EventStatus.EXPIRED;

@Service
@Slf4j
@RequiredArgsConstructor
public class RetentionCleanupService {

    private final EventRepository eventRepository;
    private final PhotoRepository photoRepository;
    private final RekognitionFaceRepository rekognitionFaceRepository;
    private final DownloadRepository downloadRepository;
    private final GuestSearchRepository guestSearchRepository;
    private final StorageService storageService;
    private final FaceService faceService;

    @Value("${aws.rekognition.collection-id-prefix}")
    private String collectionIdPrefix;

    @Scheduled(cron = "0 0 4 * * *") // Every day at 4 AM
    @Transactional
    public void cleanupExpiredEvents() {
        LOGGER.info("Starting expired events cleanup");
        
        LocalDateTime now = LocalDateTime.now();
        List<Event> expiredEvents = eventRepository.findAll().stream()
                .filter(e -> e.getExpiresAt() != null && e.getExpiresAt().isBefore(now))
                .filter(e -> e.getStatus() != EXPIRED)
                .toList();

        for (Event event : expiredEvents) {
            LOGGER.info("Cleaning up expired event: {}", event.getId());
            
            // Delete S3 assets (originals, thumbnails)
            storageService.deletePrefix(String.format("events/%d/", event.getId()));
            
            // Delete Rekognition collection
            faceService.deleteCollection(collectionIdPrefix + event.getId());

            // Delete DB records
            rekognitionFaceRepository.deleteByEvent(event);
            downloadRepository.deleteByEvent(event);
            guestSearchRepository.deleteByEvent(event);
            photoRepository.deleteByEvent(event);
            
            event.setStatus(EXPIRED);
            eventRepository.save(event);
        }
        
        LOGGER.info("Finished expired events cleanup");
    }

    @Scheduled(fixedDelay = 3600000) // Every hour
    @Transactional
    public void cleanupTempSelfies() {
        LOGGER.info("Starting temporary selfies cleanup");
        LocalDateTime threshold = LocalDateTime.now().minusHours(1);
        List<GuestSearch> searches = guestSearchRepository.findBySelfieDeletedAtIsNullAndCreatedAtBefore(threshold);
        
        for (GuestSearch search : searches) {
            if (search.getSelfieS3Key() != null) {
                try {
                    storageService.delete(search.getSelfieS3Key());
                    search.setSelfieDeletedAt(LocalDateTime.now());
                    guestSearchRepository.save(search);
                    LOGGER.info("Cleaned up temp selfie: {}", search.getSelfieS3Key());
                } catch (Exception e) {
                    LOGGER.error("Failed to delete selfie {}: {}", search.getSelfieS3Key(), e.getMessage());
                }
            }
        }
    }
}
