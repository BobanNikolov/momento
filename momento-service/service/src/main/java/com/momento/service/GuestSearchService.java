package com.momento.service;

import com.momento.data.model.Event;
import com.momento.data.model.GuestSearch;
import com.momento.data.model.Photo;
import com.momento.data.repository.EventRepository;
import com.momento.data.repository.GuestSearchRepository;
import com.momento.data.repository.PhotoRepository;
import com.momento.exception.BusinessException;
import com.momento.exception.ResourceNotFoundException;
import com.momento.service.dto.in.GuestSearchRequest;
import com.momento.service.dto.out.GuestSearchResponse;
import com.momento.service.dto.out.PhotoResponse;
import com.momento.service.integration.FaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuestSearchService {

    private final EventRepository eventRepository;
    private final GuestSearchRepository guestSearchRepository;
    private final PhotoRepository photoRepository;
    private final FaceService faceService;
    private final PhotoService photoService;

    @Value("${aws.rekognition.collection-id-prefix}")
    private String collectionIdPrefix;

    @Transactional
    public GuestSearchResponse searchPhotos(String slug, GuestSearchRequest request) {
        LOGGER.info("Searching photos for event slug: {}", slug);
        Event event = eventRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    LOGGER.warn("Search failed: Event not found for slug {}", slug);
                    return new ResourceNotFoundException("Event not found");
                });

        if (!request.getConsentAccepted()) {
            LOGGER.warn("Search failed: Consent not accepted for event slug {}", slug);
            throw new BusinessException("Consent not accepted");
        }

        byte[] selfieBytes = Base64.getDecoder().decode(request.getSelfie());

        String collectionId = collectionIdPrefix + event.getId();
        List<String> matchedExternalIds = faceService.searchByImage(collectionId, selfieBytes);

        List<Long> photoIds = matchedExternalIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        List<Photo> matchedPhotos = photoRepository.findAllById(photoIds);

        GuestSearch search = new GuestSearch();
        search.setEvent(event);
        search.setConsentAccepted(true);
        search.setConsentPolicyVersion(request.getConsentPolicyVersion());
        search.setSearchedAt(LocalDateTime.now());
        search.setResultCount(matchedPhotos.size());

        guestSearchRepository.save(search);

        List<PhotoResponse> photoResponses = matchedPhotos.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        GuestSearchResponse response = new GuestSearchResponse();
        response.setMatchedPhotos(photoResponses);
        LOGGER.info("Search completed for event slug: {}. Found {} matches.", slug, matchedPhotos.size());
        return response;
    }

    private PhotoResponse mapToResponse(Photo photo) {
        PhotoResponse pr = new PhotoResponse();
        pr.setId(photo.getId());
        pr.setFileName(photo.getFileName());
        pr.setThumbnailS3Key(photo.getThumbnailS3Key());
        pr.setDownloadUrl(photoService.getDownloadUrl(photo.getId()));
        return pr;
    }
}
