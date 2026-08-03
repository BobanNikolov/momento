package com.momento.controller;

import com.momento.service.dto.in.AssignPhotographerRequest;
import com.momento.service.dto.in.EventRequest;
import com.momento.service.dto.out.EventResponse;
import com.momento.service.dto.out.PhotographerResponse;
import com.momento.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.createEvent(request));
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/my-events")
    public ResponseEntity<List<EventResponse>> getMyEvents(Principal principal) {
        return ResponseEntity.ok(eventService.getMyEvents(principal.getName()));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<EventResponse> getEventBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(eventService.getEventBySlug(slug));
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PutMapping("/{id:[0-9]+}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{eventId}/expire")
    public ResponseEntity<Void> expireEvent(@PathVariable Long eventId) {
        eventService.expireEvent(eventId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{eventId}/photographers")
    public ResponseEntity<Void> assignPhotographer(@PathVariable Long eventId, @Valid @RequestBody AssignPhotographerRequest request) {
        eventService.assignPhotographer(eventId, request.getPhotographerId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{eventId}/photographers/{photographerId}")
    public ResponseEntity<Void> removePhotographer(@PathVariable Long eventId, @PathVariable Long photographerId) {
        eventService.removePhotographer(eventId, photographerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{eventId}/photographers")
    public ResponseEntity<List<PhotographerResponse>> listPhotographers(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.listPhotographers(eventId));
    }
}
