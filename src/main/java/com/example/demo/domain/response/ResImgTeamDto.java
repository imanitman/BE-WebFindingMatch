package com.example.demo.domain.response;

import org.springframework.core.io.Resource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResImgTeamDto {
    private long id;
    private String name;
    private String description;
    private String logo;
    private String category;
}
