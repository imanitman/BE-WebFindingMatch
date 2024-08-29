package com.example.demo.domain;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private String name;
    
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private String logo;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    @JsonIgnore
    List<User> user;

    @OneToMany(mappedBy = "team1", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Match> matchesAsTeam1;

    @OneToMany(mappedBy = "team2", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Match> matchesAsTeam2;
}
