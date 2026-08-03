package com.momento.service;

import com.momento.data.model.Event;
import com.momento.data.model.EventPhotographer;
import com.momento.data.model.UserAccount;
import com.momento.data.model.enums.EventStatus;
import com.momento.data.repository.EventPhotographerRepository;
import com.momento.data.repository.EventRepository;
import com.momento.data.repository.UserRepository;
import com.momento.exception.DuplicateResourceException;
import com.momento.exception.ResourceNotFoundException;
import com.momento.service.dto.in.EventRequest;
import com.momento.service.dto.out.EventResponse;
import com.momento.service.dto.out.PhotographerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventPhotographerRepository eventPhotographerRepository;
    private final UserRepository userRepository;

    @Transactional
    public EventResponse createEvent(EventRequest request) {
        LOGGER.info("Creating event with name: {} and slug: {}", request.getName(), request.getSlug());
        if (eventRepository.findBySlug(request.getSlug()).isPresent()) {
            LOGGER.warn("Failed to create event: Slug {} already exists", request.getSlug());
            throw new DuplicateResourceException("Slug already exists");
        }

        Event event = new Event();
        event.setName(request.getName());
        event.setSlug(request.getSlug());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setStatus(EventStatus.DRAFT);
        event.setRetentionDays(request.getRetentionDays() != null ? request.getRetentionDays() : 30);

        event = eventRepository.save(event);

        return mapToResponse(event);
    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<EventResponse> getMyEvents(String email) {
        LOGGER.debug("Fetching events for photographer: {}", email);
        UserAccount photographer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Photographer not found"));

        return eventPhotographerRepository.findByPhotographer(photographer).stream()
                .map(ep -> mapToResponse(ep.getEvent()))
                .collect(Collectors.toList());
    }

    public EventResponse getEventBySlug(String slug) {
        LOGGER.debug("Fetching event by slug: {}", slug);
        return eventRepository.findBySlug(slug)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    public EventResponse getEventById(Long id) {
        LOGGER.debug("Fetching event by ID: {}", id);
        return eventRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request) {
        LOGGER.info("Updating event ID: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        
        event.setName(request.getName());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setRetentionDays(request.getRetentionDays());
        
        return mapToResponse(eventRepository.save(event));
    }

    @Transactional
    public void deleteEvent(Long id) {
        LOGGER.info("Deleting event ID: {}", id);
        eventRepository.deleteById(id);
    }

    @Transactional
    public void expireEvent(Long id) {
        LOGGER.info("Expiring event ID: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        event.setStatus(EventStatus.EXPIRED);
        event.setExpiresAt(LocalDateTime.now());
        eventRepository.save(event);
    }

    @Transactional
    public void assignPhotographer(Long eventId, Long photographerId) {
        LOGGER.info("Assigning photographer ID: {} to event ID: {}", photographerId, eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        UserAccount photographer = userRepository.findById(photographerId)
                .orElseThrow(() -> new ResourceNotFoundException("Photographer not found"));

        if (!eventPhotographerRepository.existsByEventAndPhotographer(event, photographer)) {
            EventPhotographer assignment = new EventPhotographer();
            assignment.setEvent(event);
            assignment.setPhotographer(photographer);
            eventPhotographerRepository.save(assignment);
        }
    }

    @Transactional
    public void removePhotographer(Long eventId, Long photographerId) {
        LOGGER.info("Removing photographer ID: {} from event ID: {}", photographerId, eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        UserAccount photographer = userRepository.findById(photographerId)
                .orElseThrow(() -> new ResourceNotFoundException("Photographer not found"));
        
        eventPhotographerRepository.deleteByEventAndPhotographer(event, photographer);
    }

    public List<PhotographerResponse> listPhotographers(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        
        return eventPhotographerRepository.findByEvent(event).stream()
                .map(ep -> new PhotographerResponse(ep.getPhotographer().getId(), ep.getPhotographer().getEmail()))
                .collect(Collectors.toList());
    }

    public EventResponse mapToResponse(Event event) {
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setName(event.getName());
        response.setSlug(event.getSlug());
        response.setEventDate(event.getEventDate());
        response.setLocation(event.getLocation());
        response.setStatus(event.getStatus());
        response.setRetentionDays(event.getRetentionDays());
        response.setCreatedAt(event.getCreatedAt());
        response.setExpiresAt(event.getExpiresAt());
        return response;
    }
}
