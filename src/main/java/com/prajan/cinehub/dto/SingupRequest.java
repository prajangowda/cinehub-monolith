package com.prajan.cinehub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SingupRequest {

    private String name;

    private String email;

    private String password;


}
