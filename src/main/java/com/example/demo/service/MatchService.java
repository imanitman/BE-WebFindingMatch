package com.example.demo.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.Match;
import com.example.demo.domain.User;
import com.example.demo.domain.request.MatchRequestDto;
import com.example.demo.domain.response.ResAllMatches;
import com.example.demo.domain.response.ResMatchDto;
import com.example.demo.domain.response.ResSameTypeMatch;
import com.example.demo.domain.response.ResultPaginationDto;
import com.example.demo.repository.MatchRepository;
import com.example.demo.util.error.InvalidException;

@Service
public class MatchService {
    private final MatchRepository matchRepository;
    private final TeamService teamService;
    private final UserService userService;

    public MatchService(MatchRepository matchRepository, TeamService teamService, UserService userService){
        this.matchRepository = matchRepository;
        this.teamService = teamService;
        this.userService = userService;
    }
    public Match createNewMatch(Match match){
        return this.matchRepository.save(match);
    }
    public Match convertDtoToMatch(MatchRequestDto matchRequestDto){
        Match match = new Match();
        match.setAddress(matchRequestDto.getAddress());
        match.setMatchDate(matchRequestDto.getMatchDate());
        match.setMatchTime(matchRequestDto.getMatchTime());
        match.setType(matchRequestDto.getType());
        match.setTeam1(matchRequestDto.getTeam());
        return match;
    }
    public Match fetchMatchById(long id){
        return this.matchRepository.findById(id);
    }
    public void deleteMatch(long id){
        this.matchRepository.deleteById(id);
    }
    public ResAllMatches fetchAllMatchs(int page, int size) throws InvalidException, IOException{
        Pageable pageable = PageRequest.of(page, size);
        Page<Match> matches = this.matchRepository.findAll(pageable);
        List<Match> pageMatch = matches.getContent();
        List<ResMatchDto> resAllMatches = pageMatch.stream()
            .map(this::convertToResMatch)
            .collect(Collectors.toList());
        ResAllMatches allMatches = new ResAllMatches();
        allMatches.setMatches(resAllMatches);
        allMatches.setNumberOfElement(matches.getNumberOfElements());
        allMatches.setTotalElement(matches.getTotalElements());
        allMatches.setTotalPage(matches.getTotalPages());
        return allMatches;
    }
    public ResMatchDto convertToResMatch(Match match){
        ResMatchDto resMatchDto = new ResMatchDto();
        resMatchDto.setId(match.getId());
        resMatchDto.setAddress(match.getAddress());
        resMatchDto.setMatchDate(match.getMatchDate());
        resMatchDto.setMatchTime(match.getMatchTime());
        resMatchDto.setStadiumName(match.getStadiumName());
        resMatchDto.setTeam1(this.teamService.convertToResInTeam(match.getTeam1()));
        if(match.getTeam2() != null){
            resMatchDto.setTeam2(this.teamService.convertToResInTeam(match.getTeam2()));
        }
        else{
            resMatchDto.setTeam2(null);
        }
        resMatchDto.setType(match.getType());
        resMatchDto.setStatus(match.isStatus());
        return resMatchDto;
    }
    public ResSameTypeMatch fetchSameTypeMatch(String type, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Match> matches = this.matchRepository.findByType(type,pageable);
        List<Match> pageMatch = matches.getContent();
        List<ResMatchDto> allResMatch = pageMatch.stream()
            .map(this::convertToResMatch)
            .collect(Collectors.toList());
        ResSameTypeMatch resSameTypeMatch = new ResSameTypeMatch();
        resSameTypeMatch.setMatches(allResMatch);
        resSameTypeMatch.setType(type);
        resSameTypeMatch.setTotalPage(matches.getTotalPages());
        resSameTypeMatch.setTotalElement(matches.getTotalElements());
        resSameTypeMatch.setNumberOfElement(matches.getNumberOfElements());
        return resSameTypeMatch;

    }
    public List<Match> fetchAllYourMatch(User user){
        return this. matchRepository.findByUser(user);
    }
    public void deleteMatchById(long id){
        this.matchRepository.deleteById(id);
    }
}
