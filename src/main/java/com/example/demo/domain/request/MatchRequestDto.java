package com.example.demo.domain.request;


import com.example.demo.domain.Team;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchRequestDto {
    private Team team;
    private String stadiumName;
    private String address;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String matchDate;
    @JsonFormat(pattern = "HH:mm")
    private String matchTime;
    private String type;
}
