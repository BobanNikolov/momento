package com.momento.service.dto.out;

import lombok.Data;
import java.util.List;

@Data
public class GuestSearchResponse {
    private List<PhotoResponse> matchedPhotos;
}
