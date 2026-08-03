package com.momentoworker.service.integration.impl;

import com.momentoworker.service.integration.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Profile("local")
@Slf4j
public class LocalStorageService implements StorageService {

    @Value("${storage.local.dir}")
    private String storageDir;

    @Override
    public byte[] getBytes(String key) {
        try {
            return Files.readAllBytes(Paths.get(storageDir, key));
        } catch (IOException e) {
            LOGGER.error("Failed to read file: {}", key, e);
            throw new RuntimeException("Failed to read file", e);
        }
    }

    @Override
    public void putBytes(String key, byte[] bytes, String contentType) {
        try {
            Path path = Paths.get(storageDir, key);
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);
        } catch (IOException e) {
            LOGGER.error("Failed to write file: {}", key, e);
            throw new RuntimeException("Failed to write file", e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            Files.deleteIfExists(Paths.get(storageDir, key));
        } catch (IOException e) {
            LOGGER.error("Failed to delete file: {}", key, e);
        }
    }

    @Override
    public void deletePrefix(String prefix) {
        try {
            Path start = Paths.get(storageDir, prefix);
            if (Files.exists(start)) {
                Files.walk(start)
                        .sorted((a, b) -> b.compareTo(a)) // Delete files before directories
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                LOGGER.error("Failed to delete path: {}", p, e);
                            }
                        });
            }
        } catch (IOException e) {
            LOGGER.error("Failed to delete prefix: {}", prefix, e);
        }
    }

    @Override
    public boolean isHealthy() {
        File dir = new File(storageDir);
        return dir.exists() && dir.isDirectory() && dir.canWrite();
    }
}
