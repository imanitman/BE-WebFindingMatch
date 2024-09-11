package com.example.demo.domain.request;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.demo.domain.Team;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchRequestDto {
    private Team  team;
    private String address;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate matchDate;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime matchTime;
    private String type;
}
