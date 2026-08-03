package com.momento.service.dto.out;

import com.momento.data.model.enums.EventStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EventResponse {
    private Long id;
    private String name;
    private String slug;
    private LocalDate eventDate;
    private String location;
    private EventStatus status;
    private Integer retentionDays;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String qrCodeUrl;
}
