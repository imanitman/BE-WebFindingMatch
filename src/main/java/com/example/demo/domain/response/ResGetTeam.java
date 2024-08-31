package com.example.demo.domain.response;

import org.springframework.core.io.Resource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResGetTeam {
    private Resource fileLogo;
    private String name;
    private String description;

}
