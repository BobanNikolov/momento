package com.momento.service.dto.in;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EventRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String slug;

    private LocalDate eventDate;
    private String location;
    private Integer retentionDays;
}
