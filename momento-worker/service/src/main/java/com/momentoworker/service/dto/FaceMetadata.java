package com.momentoworker.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FaceMetadata {
    private String faceId;
    private String externalImageId;
    private Float confidence;
    private BoundingBox boundingBox;

    @Data
    @Builder
    public static class BoundingBox {
        private Float width;
        private Float height;
        private Float left;
        private Float top;

        @Override
        public String toString() {
            return String.format("{\"Width\":%f,\"Height\":%f,\"Left\":%f,\"Top\":%f}", width, height, left, top);
        }
    }
}
