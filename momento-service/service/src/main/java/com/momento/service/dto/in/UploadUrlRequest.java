package com.momento.service.dto.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class UploadUrlRequest {
    @NotEmpty
    private List<@NotBlank String> fileNames;
}
