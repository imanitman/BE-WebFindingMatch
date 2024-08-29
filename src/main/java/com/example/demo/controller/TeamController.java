package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.domain.Team;
import com.example.demo.domain.request.ReqCreateTeam;
import com.example.demo.service.TeamService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class TeamController {
    private final TeamService teamService;
    public String urlImage = "D://SideProject/FindingMatch/Backend/Image/";

    public TeamController (TeamService teamService){
        this.teamService = teamService;
    }
   
    /**
     * @param reqCreateTeam
     * @return
     */
    @PostMapping("/teams")
    public String createNewTeam(@RequestParam ("name") String name,
        @RequestParam ("description") String desc,
        @RequestParam ("logo") MultipartFile file) {
        Team currenTeam = new Team();
        if(name != null){
            currenTeam.setName(name);
            currenTeam.setDescription(desc);
        }
        if (file != null && !file.isEmpty()){
            try {
                String fileName = file.getOriginalFilename();
                Path path  = Paths.get(urlImage+fileName);
                currenTeam.setLogo(path.toString());
                Files.copy(file.getInputStream(), path);
                this.teamService.createTeam(currenTeam);
                return "done";
            } catch (Exception e) {
                return "error";
            }
        }
        return "fail to connect";
    }
}
