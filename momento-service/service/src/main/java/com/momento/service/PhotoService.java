package com.momento.service;

import com.momento.data.model.Event;
import com.momento.data.model.Photo;
import com.momento.data.model.enums.PhotoStatus;
import com.momento.data.model.UserAccount;
import com.momento.data.repository.EventRepository;
import com.momento.data.repository.PhotoRepository;
import com.momento.exception.ResourceNotFoundException;
import com.momento.service.dto.in.UploadUrlRequest;
import com.momento.service.dto.out.PhotoResponse;
import com.momento.service.dto.out.ProcessingStatusResponse;
import com.momento.service.dto.out.UploadUrlResponse;
import com.momento.service.integration.QueueService;
import com.momento.service.integration.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final EventRepository eventRepository;
    private final StorageService storageService;
    private final QueueService queueService;

    @Transactional
    public UploadUrlResponse getUploadUrls(Long eventId, UploadUrlRequest request, UserAccount user) {
        LOGGER.info("Generating upload URLs for event ID: {} ({} files) by user: {}", eventId, request.getFileNames().size(), user.getEmail());
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        List<UploadUrlResponse.UploadUrlItem> items = new ArrayList<>();

        for (String fileName : request.getFileNames()) {
            Photo photo = new Photo();
            photo.setEvent(event);
            photo.setUploadedBy(user);
            photo.setFileName(fileName);
            photo.setProcessingStatus(PhotoStatus.UPLOADED);
            photo.setOriginalS3Key("pending");
            
            photo = photoRepository.save(photo);
            
            String s3Key = String.format("events/%d/originals/%d.jpg", eventId, photo.getId());
            photo.setOriginalS3Key(s3Key);
            photoRepository.save(photo);

            String uploadUrl = storageService.generateUploadUrl(s3Key, "image/jpeg");

            items.add(new UploadUrlResponse.UploadUrlItem(photo.getId(), fileName, uploadUrl));
        }

        return new UploadUrlResponse(items);
    }

    @Transactional
    public void confirmUpload(Long photoId) {
        LOGGER.info("Confirming upload for photo ID: {}", photoId);
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found"));

        photo.setProcessingStatus(PhotoStatus.QUEUED);
        photoRepository.save(photo);
        LOGGER.debug("Photo ID: {} enqueued for processing", photoId);
        queueService.enqueuePhoto(photoId);
    }

    public String getDownloadUrl(Long photoId) {
        LOGGER.debug("Generating download URL for photo ID: {}", photoId);
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found"));
        return storageService.getDownloadUrl(photo.getOriginalS3Key());
    }

    public List<PhotoResponse> listPhotos(Long eventId) {
        LOGGER.debug("Listing photos for event ID: {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return photoRepository.findByEvent(event).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProcessingStatusResponse getProcessingStatus(Long eventId) {
        LOGGER.debug("Fetching processing status for event ID: {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        
        List<Photo> photos = photoRepository.findByEvent(event);
        Map<PhotoStatus, Long> counts = photos.stream()
                .collect(Collectors.groupingBy(Photo::getProcessingStatus, Collectors.counting()));
        
        for (PhotoStatus status : PhotoStatus.values()) {
            counts.putIfAbsent(status, 0L);
        }
        
        return new ProcessingStatusResponse(counts);
    }

    @Transactional
    public void reprocessAll(Long eventId) {
        LOGGER.info("Reprocessing all photos for event ID: {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        
        List<Photo> photos = photoRepository.findByEvent(event);
        LOGGER.info("Enqueuing {} photos for reprocessing in event ID: {}", photos.size(), eventId);
        for (Photo photo : photos) {
            photo.setProcessingStatus(PhotoStatus.QUEUED);
            photoRepository.save(photo);
            queueService.enqueuePhoto(photo.getId());
        }
    }

    private PhotoResponse mapToResponse(Photo photo) {
        PhotoResponse pr = new PhotoResponse();
        pr.setId(photo.getId());
        pr.setFileName(photo.getFileName());
        pr.setThumbnailS3Key(photo.getThumbnailS3Key());
        pr.setDownloadUrl(getDownloadUrl(photo.getId()));
        return pr;
    }
}
