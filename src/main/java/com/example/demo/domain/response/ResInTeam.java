package com.example.demo.domain.response;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Getter
@Setter
public class ResInTeam {
    private long id;
    private String name;
    private String description;
    private String logo;
    private String category;
    private List<ResUserDto> user;
}
