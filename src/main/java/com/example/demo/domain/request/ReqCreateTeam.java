package com.example.demo.domain.request;



import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateTeam {
    private String name;
    private String description;
    private MultipartFile logo;
}
