package com.momento.service.integration.impl;

import com.momento.service.integration.QueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("local")
public class LocalQueueService implements QueueService {
    @Override
    public void enqueuePhoto(Long photoId) {
        LOGGER.debug("Local queue: photo ID {} is ready for processing (polling-based)", photoId);
        // In local mode, we rely on DB polling of 'QUEUED' photos.
        // PhotoService sets the status to QUEUED, which is enough for the local poller to pick it up.
    }
}
