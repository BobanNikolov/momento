package com.momentoworker.service.integration;

import com.momentoworker.data.model.Event;
import com.momentoworker.data.model.Photo;
import com.momentoworker.service.dto.FaceMetadata;

import java.util.List;

public interface FaceService {
    void ensureCollection(Event event);
    List<FaceMetadata> indexFace(String collectionId, Photo photo, byte[] bytes);
    void deleteCollection(String collectionId);
}
