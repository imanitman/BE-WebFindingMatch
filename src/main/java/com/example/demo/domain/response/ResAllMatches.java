package com.example.demo.domain.response;

import java.util.List;

import com.example.demo.domain.Match;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResAllMatches {
    private List<ResMatchDto> matches;
    private int totalPage;
    private long totalElement;
    private int numberOfElement;
}
