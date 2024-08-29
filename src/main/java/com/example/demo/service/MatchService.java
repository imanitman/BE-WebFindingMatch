package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Match;
import com.example.demo.domain.User;
import com.example.demo.domain.request.MatchRequestDto;
import com.example.demo.domain.response.ResAllMatches;
import com.example.demo.domain.response.ResSameTypeMatch;
import com.example.demo.domain.response.ResultPaginationDto;
import com.example.demo.repository.MatchRepository;

@Service
public class MatchService {
    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository){
        this.matchRepository = matchRepository;
    }
    public Match createNewMatch(Match match){
        return  this.matchRepository.save(match);
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
    public ResAllMatches fetchAllMatchs(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Match> matches = this.matchRepository.findAll(pageable);
        List<Match> pageMatch = matches.getContent();
        ResAllMatches allMatches = new ResAllMatches();
        allMatches.setMatches(pageMatch);
        allMatches.setNumberOfElement(matches.getNumberOfElements());
        allMatches.setTotalElement(matches.getTotalElements());
        allMatches.setTotalPage(matches.getTotalPages());
        return allMatches;
    }
    public ResSameTypeMatch fetchSameTypeMatch(String type, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Match> matches = this.matchRepository.findByType(type,pageable);
        List<Match> pageMatch = matches.getContent();
        ResSameTypeMatch resSameTypeMatch = new ResSameTypeMatch();
        resSameTypeMatch.setMatches(pageMatch);
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
