package com.momento.service.integration.impl;

import com.momento.data.model.enums.PhotoStatus;
import com.momento.data.repository.PhotoRepository;
import com.momento.service.integration.FaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Profile("local")
@RequiredArgsConstructor
public class LocalFaceService implements FaceService {

    private final PhotoRepository photoRepository;

    @Override
    public List<String> searchByImage(String collectionId, byte[] selfieBytes) {
        LOGGER.info("Performing local face search for collection ID: {}", collectionId);
        // Dev-only deterministic match: return all PROCESSED photos for the event
        try {
            String idStr = collectionId.replace("momento_event_", "");
            Long eventId = Long.parseLong(idStr);
            List<String> results = photoRepository.findByEventIdAndProcessingStatus(eventId, PhotoStatus.PROCESSED)
                    .stream()
                    .map(photo -> photo.getId().toString())
                    .collect(Collectors.toList());
            LOGGER.info("Local face search completed for collection ID: {}. Found {} matches.", collectionId, results.size());
            return results;
        } catch (Exception e) {
            LOGGER.error("Local face search failed for collection ID: {}", collectionId, e);
            return List.of();
        }
    }
}
