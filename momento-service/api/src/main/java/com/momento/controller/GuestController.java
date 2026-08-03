package com.momento.controller;

import com.momento.service.dto.out.EventResponse;
import com.momento.service.dto.in.GuestSearchRequest;
import com.momento.service.dto.out.GuestSearchResponse;
import com.momento.service.EventService;
import com.momento.service.GuestSearchService;
import com.momento.service.PhotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class GuestController {

    private final EventService eventService;
    private final GuestSearchService guestSearchService;
    private final PhotoService photoService;

    @GetMapping("/event/{slug}")
    public ResponseEntity<EventResponse> getEventPublic(@PathVariable String slug) {
        return ResponseEntity.ok(eventService.getEventBySlug(slug));
    }

    @PostMapping("/event/{slug}/search")
    public ResponseEntity<GuestSearchResponse> searchPhotos(
            @PathVariable String slug,
            @RequestBody @Valid GuestSearchRequest request
    ) {
        return ResponseEntity.ok(guestSearchService.searchPhotos(slug, request));
    }

    @GetMapping("/photos/{photoId}/download-url")
    public ResponseEntity<String> getDownloadUrl(@PathVariable Long photoId) {
        return ResponseEntity.ok(photoService.getDownloadUrl(photoId));
    }
}
