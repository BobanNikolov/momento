package com.momento.service.integration.impl;

import com.momento.service.integration.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
@Profile("local")
@RequiredArgsConstructor
public class LocalStorageService implements StorageService {

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${storage.local.dir}")
    private String storageDir;

    @Override
    public String generateUploadUrl(String key, String contentType) {
        LOGGER.debug("Generating local upload URL for key: {}", key);
        return applicationUrl + "/api/local/storage?key=" + key;
    }

    @Override
    public String getDownloadUrl(String key) {
        LOGGER.debug("Generating local download URL for key: {}", key);
        return applicationUrl + "/api/local/storage?key=" + key;
    }

    @Override
    public boolean isHealthy() {
        File dir = new File(storageDir);
        return dir.exists() && dir.isDirectory() && dir.canWrite();
    }
}
