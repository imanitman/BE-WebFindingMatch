package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Match;
import com.example.demo.domain.request.MatchRequestDto;
import com.example.demo.service.MatchService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class MatchControlller {
    private final MatchService matchService;

    public MatchControlller(MatchService matchService){
        this.matchService = matchService;
    }
    @PostMapping("/matchs")
    public ResponseEntity<Match> createNewMatch(@RequestBody MatchRequestDto entity){
        Match match = new Match();
        match = this.matchService.convertDtoToMatch(entity);
        return  ResponseEntity.ok().body(this.matchService.createNewMatch(match));
    }
    @GetMapping("/matchs/{id}")
    public ResponseEntity<Match> fetchDetailMatch (@PathVariable ("id") long id) {
        return ResponseEntity.ok().body(this.matchService.fetchMatchById(id));
    }
    @DeleteMapping("/matchs/{id}")
    public ResponseEntity<String> deleteMatch(@PathVariable("id") long id){
        this.matchService.deleteMatch(id);
        return ResponseEntity.ok().body("Done");
    }
}
