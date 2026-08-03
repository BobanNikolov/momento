package com.momento.service.integration;

public interface StorageService {
    String generateUploadUrl(String key, String contentType);
    String getDownloadUrl(String key);
    boolean isHealthy();
}
