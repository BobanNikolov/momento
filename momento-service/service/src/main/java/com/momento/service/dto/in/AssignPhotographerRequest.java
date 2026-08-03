package com.momento.service.dto.in;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignPhotographerRequest {
    @NotNull
    private Long photographerId;
}
