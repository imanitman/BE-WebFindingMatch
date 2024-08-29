package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.domain.Team;
import com.example.demo.domain.request.ReqCreateTeam;
import com.example.demo.repository.TeamRepository;

@Service
public class TeamService {
    
    private final TeamRepository teamRepository;

    public TeamService (TeamRepository teamRepository){
        this.teamRepository = teamRepository;
    }

    public Team createTeam(Team team){
        return this.teamRepository.save(team);
    }

    public Team convertReqCreateToTeam(ReqCreateTeam reqCreateTeam){
        Team current_team = new Team();
        current_team.setDescription(reqCreateTeam.getDescription());
        current_team.setName(reqCreateTeam.getName());
        return current_team;
    }
}
