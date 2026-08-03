package com.momento.service.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadUrlResponse {
    private List<UploadUrlItem> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UploadUrlItem {
        private Long photoId;
        private String fileName;
        private String uploadUrl;
    }
}
