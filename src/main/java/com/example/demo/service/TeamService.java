package com.example.demo.service;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import com.example.demo.domain.Team;
import com.example.demo.domain.request.ReqCreateTeam;
import com.example.demo.domain.response.ResInTeam;
import com.example.demo.domain.response.ResUserDto;
import com.example.demo.repository.TeamRepository;

@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserService userService;

    public TeamService (TeamRepository teamRepository, UserService userService){
        this.teamRepository = teamRepository;
        this.userService = userService;
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
    public Team fetchTeamById(long id){
        return this.teamRepository.findById(id);
    }
    public void deleteTeam(long id){
        this.teamRepository.deleteById(id);
    }
    public ResInTeam convertToResInTeam(Team team){
        ResInTeam resInTeam = new ResInTeam();
        resInTeam.setId(team.getId());
        resInTeam.setLogo(team.getLogo());
        resInTeam.setName(team.getName());
        resInTeam.setDescription(team.getDescription());
        List<ResUserDto> allPlayer = team.getUser().stream()
            .map(this.userService::convertToResUserDto)
            .collect(Collectors.toList());
        resInTeam.setUser(allPlayer);
        return resInTeam;
    }
}
