package com.approvalhub.dto.user;

import com.approvalhub.domain.enums.Role;
import lombok.Data;

import java.time.Instant;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private Role  role;
    private Instant createdAt;
}
