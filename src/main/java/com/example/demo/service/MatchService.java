package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.domain.Match;
import com.example.demo.domain.request.MatchRequestDto;
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
        match.setTeam(matchRequestDto.getName());
        match.setTime(matchRequestDto.getMatch_time());
        match.setType(matchRequestDto.getType());
        return match;
    }
    public Match fetchMatchById(long id){
        return this.matchRepository.findById(id);
    }
    public void deleteMatch(long id){
        this.matchRepository.deleteById(id);
    }
}
