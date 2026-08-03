package com.momento.service.dto.out;

import lombok.Data;

@Data
public class PhotoResponse {
    private Long id;
    private String fileName;
    private String thumbnailS3Key;
    private String downloadUrl;
}
