package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.domain.Team;
import com.example.demo.domain.User;
import com.example.demo.domain.request.ReqCreateTeam;
import com.example.demo.domain.response.ResGetTeam;
import com.example.demo.domain.response.ResTeamDto;
import com.example.demo.service.TeamService;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.List;
import java.util.ArrayList;



@RestController
public class TeamController {
    private final TeamService teamService;
    private final UserService userService;
    public String urlImage = "D://SideProject/FindingMatch/Backend/Image/";

    public TeamController (TeamService teamService, UserService userService){
        this.teamService = teamService;
        this.userService = userService;
    }
    /**
     * @param reqCreateTeam
     * @return
     */
    @PostMapping("/teams")
    public ResponseEntity<String> createNewTeam(@RequestParam ("name") String name,
        @RequestParam ("description") String desc,
        @RequestPart ("logo") MultipartFile file,
        @RequestParam ("category") String category) {
        Team currenTeam = new Team();
        if(name != null){
            currenTeam.setName(name);
            currenTeam.setDescription(desc);
            currenTeam.setCategory(category);
            String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get() :"null";
            User currentUser = this.userService.fetchUserByEmail(email);
            List<User> users = new ArrayList<User>();
            users.add(currentUser);
            currenTeam.setUser(users);
        }
        if (file != null && !file.isEmpty()){
            try {
                String fileName = file.getOriginalFilename();
                if(fileName != null && !fileName.matches(".*\\.(png|jepg|jpg)$")){
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body("Chỉ chấp nhận file định dạng PNG, JPEG, JPG");
                }
                Path path  = Paths.get(urlImage+fileName);
                currenTeam.setLogo(path.toString());
                Files.copy(file.getInputStream(), path);
                this.teamService.createTeam(currenTeam);
                return ResponseEntity.ok().body("done");
            } catch (Exception e) {
                e.printStackTrace();
                // Hoặc trả về thông tin lỗi cho client
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.badRequest().body("file is invalid");
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
    @GetMapping("/teams/create/{category}")
    public ResponseEntity<Team> getMethodName(@PathVariable ("category") String category) {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        User current_user = this.userService.fetchUserByEmail(email);
        List<Team> teams = current_user.getTeam();
        for (Team team : teams){
            if (team.getCategory().equalsIgnoreCase(category)){
                return ResponseEntity.ok().body(team);
            }
        }
        System.out.println("Can't find");
        return ResponseEntity.ok().body(null);
    }
    @PostMapping("/team/join/{id}")
    public ResponseEntity<String> JoinTeamPage(@PathVariable ("id") long id) {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() :"null";
        Team currentTeam = this.teamService.fetchTeamById(id);
        String category =currentTeam.getCategory();
        User currentUser = this.userService.fetchUserByEmail(email);
        List<Team> teams = currentUser.getTeam();
        for (Team team : teams){
            if (team.getCategory().equals(category)){
                teams.remove(team);
            }
        }
        List<User> listMember = currentTeam.getUser();
        listMember.add(currentUser);
        currentTeam.setUser(listMember);
        return ResponseEntity.ok().body("add teamate successfull");
    }
}
