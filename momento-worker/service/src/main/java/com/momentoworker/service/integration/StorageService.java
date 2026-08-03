package com.momentoworker.service.integration;

public interface StorageService {
    byte[] getBytes(String key);
    void putBytes(String key, byte[] bytes, String contentType);
    void delete(String key);
    void deletePrefix(String prefix);
    boolean isHealthy();
}
