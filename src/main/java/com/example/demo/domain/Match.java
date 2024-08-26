package com.example.demo.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.demo.util.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "matchs")
@Getter
@Setter
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String address;
    private String team1;
    private String team2;
     @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate matchDate;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime matchTime;
    private String type;
    private boolean Status;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @PrePersist
    public void handleBeforeCreate(){
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
        ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.createdAt = Instant.now();
    }
    @PreUpdate
    public void handleBeforeUpdate(){
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
        ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.updatedAt = Instant.now();
    }
}
