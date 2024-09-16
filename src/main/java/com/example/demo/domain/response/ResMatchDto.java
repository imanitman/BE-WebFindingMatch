package com.example.demo.domain.response;

import org.springframework.core.io.Resource;
import java.time.LocalDate;
import java.time.LocalTime;
import com.example.demo.domain.Team;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResMatchDto {
    private long id;
    private String address;
    private String stadiumName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String matchDate;
    @JsonFormat(pattern = "HH:mm:ss")
    private String matchTime;
    private String type;
    private boolean status;
    private ResInTeam team1;
    private ResInTeam team2;
}
