package com.momento.service.dto.in;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfirmUploadRequest {
    @NotNull
    private Long photoId;
}
