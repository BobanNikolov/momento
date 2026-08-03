package com.momento.service.dto.out;

import com.momento.data.model.enums.PhotoStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessingStatusResponse {
    private Map<PhotoStatus, Long> counts;
}
