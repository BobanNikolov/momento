package com.momento.controller;

import com.momento.service.dto.in.ConfirmUploadRequest;
import com.momento.service.dto.in.UploadUrlRequest;
import com.momento.service.dto.out.PhotoResponse;
import com.momento.service.dto.out.ProcessingStatusResponse;
import com.momento.service.dto.out.UploadUrlResponse;
import com.momento.data.model.UserAccount;
import com.momento.data.repository.UserRepository;
import com.momento.service.PhotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;
    private final UserRepository userRepository;

    @PostMapping("/event/{eventId}/photos/upload-urls")
    public ResponseEntity<UploadUrlResponse> getUploadUrls(
            @PathVariable Long eventId,
            @RequestBody @Valid UploadUrlRequest request,
            Authentication authentication
    ) {
        UserAccount user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(photoService.getUploadUrls(eventId, request, user));
    }

    @PostMapping("/photos/confirm-upload")
    public ResponseEntity<Void> confirmUpload(@RequestBody @Valid ConfirmUploadRequest request) {
        photoService.confirmUpload(request.getPhotoId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/event/{eventId}/photos")
    public ResponseEntity<List<PhotoResponse>> listPhotos(@PathVariable Long eventId) {
        return ResponseEntity.ok(photoService.listPhotos(eventId));
    }

    @GetMapping("/event/{eventId}/processing-status")
    public ResponseEntity<ProcessingStatusResponse> getProcessingStatus(@PathVariable Long eventId) {
        return ResponseEntity.ok(photoService.getProcessingStatus(eventId));
    }

    @PostMapping("/event/{eventId}/process-all")
    public ResponseEntity<Void> reprocessAll(@PathVariable Long eventId) {
        photoService.reprocessAll(eventId);
        return ResponseEntity.ok().build();
    }
}
