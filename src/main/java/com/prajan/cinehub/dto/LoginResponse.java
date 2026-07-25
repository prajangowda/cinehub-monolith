package com.prajan.cinehub.dto;


import com.prajan.cinehub.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;

    private Role role;

    private Boolean profilecompleted;

    public LoginResponse(String token, Boolean profilecompleted) {
        this.token = token;
        this.profilecompleted = profilecompleted;
    }

    public LoginResponse(String token, Role role) {
        this.token = token;
        this.role = role;
    }

    public void setToken(String token, Role role) {
        this.token = token;
        this.role=role;
    }


}
