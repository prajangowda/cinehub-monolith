package com.prajan.cinehub.auth.dto;

import com.prajan.cinehub.auth.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
}
