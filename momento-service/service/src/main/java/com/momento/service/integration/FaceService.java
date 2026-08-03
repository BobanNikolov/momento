package com.momento.service.integration;

import java.util.List;

public interface FaceService {
    List<String> searchByImage(String collectionId, byte[] selfieBytes);
}
