package com.example.demo.domain.request;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchRequestDto {
    private String name;
    private String address;
    private LocalDateTime match_time;
    private String type;
}
