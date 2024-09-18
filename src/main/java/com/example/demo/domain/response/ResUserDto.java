package com.example.demo.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResUserDto {
    private long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
}
