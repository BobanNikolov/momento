package com.momento.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/local/storage")
@Profile("local")
@Slf4j
public class LocalStorageController {

    @Value("${storage.local.dir}")
    private String storageDir;

    @PutMapping
    public ResponseEntity<Void> upload(@RequestParam String key, @RequestBody byte[] bytes) throws IOException {
        Path path = Paths.get(storageDir, key);
        Files.createDirectories(path.getParent());
        Files.write(path, bytes);
        LOGGER.info("Uploaded file to {}", path);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<byte[]> download(@RequestParam String key) throws IOException {
        Path path = Paths.get(storageDir, key);
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }
        byte[] bytes = Files.readAllBytes(path);
        String contentType = Files.probeContentType(path);
        return ResponseEntity.ok()
                .contentType(contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }
}
