package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.domain.Match;
import com.example.demo.domain.Team;
import com.example.demo.domain.User;
import com.example.demo.domain.request.MatchRequestDto;
import com.example.demo.domain.response.ResAllMatches;
import com.example.demo.domain.response.ResJoinMatch;
import com.example.demo.domain.response.ResMatchDto;
import com.example.demo.domain.response.ResSameTypeMatch;
import com.example.demo.service.MatchService;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.error.InvalidException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
public class MatchControlller {
    private final MatchService matchService;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    public MatchControlller(MatchService matchService, SecurityUtil securityUtil, UserService userService){
        this.matchService = matchService;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }
    @PostMapping("/matchs")
    public ResponseEntity<Match> createNewMatch(@RequestBody MatchRequestDto entity){
        System.out.println(entity.getTeam());
        Match match = new Match();
        match = this.matchService.convertDtoToMatch(entity);
        match.setStatus(false);
        return  ResponseEntity.ok().body(this.matchService.createNewMatch(match));
    }
    @GetMapping("/matchs/{id}")
    public ResponseEntity<ResMatchDto> fetchDetailMatch (@PathVariable ("id") long id) {
        Match currentMatch = this.matchService.fetchMatchById(id);
        return ResponseEntity.ok().body(this.matchService.convertToResMatch(currentMatch));
    }

    @DeleteMapping("/matchs/{id}")
    public ResponseEntity<String> deleteMatch(@PathVariable("id") long id){
        this.matchService.deleteMatch(id);
        return ResponseEntity.ok().body("Deleted");
    }

    @GetMapping("/matchs")
    public ResponseEntity<ResAllMatches> fetchAllMatches( @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) throws InvalidException, IOException{
                return ResponseEntity.ok().body(this.matchService.fetchAllMatchs(page, size));
            }
    @PostMapping("/matchs/join/{id}")
    public ResponseEntity<String> postMethodName(@PathVariable ("id") long id) {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "" ;
        User user = this.userService.fetchUserByEmail(email);
        Match current_match = this.matchService.fetchMatchById(id);
        String type = current_match.getType();
        List<Team> teams = user.getTeam();
        for (Team team : teams){
            if (team.getCategory() ==  type){
                current_match.setStatus(true);
                current_match.setTeam2(team);
                this.matchService.createNewMatch(current_match);
                return ResponseEntity.ok().body("Join successfully");
            }
        }
        return ResponseEntity.ok().body("You have to create a team suitable with this match");
    }
    @GetMapping("/matchs/sport/{type}")
    public ResponseEntity<ResSameTypeMatch> sameTypePage(@PathVariable ("type") String type, @RequestParam(value = "page", defaultValue = "0") int page,
    @RequestParam(value = "size", defaultValue = "10") int size) {
        ResSameTypeMatch resSameTypeMatch = this.matchService.fetchSameTypeMatch(type, page, size);
        return ResponseEntity.ok().body(resSameTypeMatch);
    }
    @GetMapping("/matchs/my")
    public ResponseEntity<List<Match>> myMatchPage() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User current_user = this.userService.fetchUserByEmail(email);
        List<Match> yourMatchs = this.matchService.fetchAllYourMatch(current_user);
        return ResponseEntity.ok().body(yourMatchs);
    }
}
