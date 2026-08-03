package com.momentoworker.service;

import com.momentoworker.data.model.Photo;
import com.momentoworker.data.model.enums.PhotoStatus;
import com.momentoworker.data.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("local")
@RequiredArgsConstructor
@Slf4j
public class LocalQueuePoller {

    private final PhotoRepository photoRepository;
    private final PhotoProcessingWorker photoProcessingWorker;

    @Scheduled(fixedDelayString = "${local.queue.poll-interval:5000}")
    public void pollQueue() {
        List<Photo> queuedPhotos = photoRepository.findByProcessingStatus(PhotoStatus.QUEUED);
        List<Photo> failedPhotos = photoRepository.findByProcessingStatus(PhotoStatus.FAILED);
        
        processPhotos(queuedPhotos);
        processPhotos(failedPhotos);
    }

    private void processPhotos(List<Photo> photos) {
        if (!photos.isEmpty()) {
            LOGGER.info("LocalQueuePoller: processing {} photos", photos.size());
            for (Photo photo : photos) {
                try {
                    photoProcessingWorker.processPhoto(photo.getId());
                } catch (Exception e) {
                    LOGGER.error("Failed to process photo {}", photo.getId(), e);
                }
            }
        }
    }
}
