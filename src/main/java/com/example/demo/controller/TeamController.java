package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.domain.Team;
import com.example.demo.domain.request.ReqCreateTeam;
import com.example.demo.domain.response.ResGetTeam;
import com.example.demo.domain.response.ResTeamDto;
import com.example.demo.service.TeamService;
import com.example.demo.util.error.InvalidException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.io.IOException;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;




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
    @GetMapping("/teams/{id}")
    public ResponseEntity<ResGetTeam> getDetailTeam(@PathVariable ("id") long id) throws InvalidException, IOException {
        Team current_team = this.teamService.fetchTeamById(id);
        String pathFile = current_team.getLogo();

        Path path = Paths.get(pathFile);
        File file = path.toFile();
        if (!file.exists()){
            throw new InvalidException("Your file had been not found ");
        }
        Resource resource = new UrlResource(file.toURI());

        //Xác định kiểu content-type
        String contentType = Files.probeContentType(path);
        if (contentType == null){
            contentType = "application/octet-stream";
        }
        ResGetTeam resGetTeam = new ResGetTeam();
        resGetTeam.setFileLogo(resource);
        resGetTeam.setDescription(current_team.getDescription());
        resGetTeam.setName(current_team.getName());
        return  ResponseEntity.ok()
                              .contentType(MediaType.parseMediaType(contentType))
                              .header(HttpHeaders.CONTENT_DISPOSITION,  "attachment; filename=\"" + file.getName() + "\"")
                              .body(resGetTeam);
    }
    @DeleteMapping("/teams/{id}")
    public ResponseEntity<String> deleteTeam (@PathVariable ("id") long id){
        this.teamService.deleteTeam(id);
        return ResponseEntity.ok().body("delete team successfull");
    }
    @PutMapping("team/{id}")
    public ResponseEntity<ResTeamDto> updateTeam(@RequestParam ("id") long id,
    @RequestParam("name") String name,
    @RequestParam("description") String description,
    @RequestParam("file") MultipartFile logo) throws IOException {
        Team current_Team = this.teamService.fetchTeamById(id);
        current_Team.setName(name);
        current_Team.setDescription(description);
        if (logo != null && logo.isEmpty()){
            String logoFile = logo.getOriginalFilename();
            Path path = Paths.get(urlImage + logoFile);
            current_Team.setLogo(logoFile);
            Files.copy(logo.getInputStream(), path);
            this.teamService.createTeam(current_Team);
            ResTeamDto resTeamDto = new ResTeamDto();
            resTeamDto.setId(current_Team.getId());
            resTeamDto.setName(current_Team.getName());
            resTeamDto.setDescription(current_Team.getDescription());
            return ResponseEntity.ok().body(resTeamDto);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }
}
