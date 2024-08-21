package com.example.demo.domain;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// @Entity
// @Table(name = "matchs")
@Getter
@Setter
public class Match {
    private String address;
    private String team;
    private Instant time;
    private String type;
}
