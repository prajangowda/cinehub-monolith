package com.prajan.cinehub.dto;



import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;

    public LoginResponse(String token) {
        this.accessToken = token;
    }
}
