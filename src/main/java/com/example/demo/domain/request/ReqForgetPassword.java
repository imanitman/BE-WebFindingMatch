package com.example.demo.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqForgetPassword {
    private String email;
    private String password;
}
