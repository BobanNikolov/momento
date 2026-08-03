package com.momento.service.dto.out;

import com.momento.data.model.enums.UserRole;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private UserRole role;
}
