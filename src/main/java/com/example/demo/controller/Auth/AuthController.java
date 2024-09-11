package com.example.demo.controller.Auth;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.domain.User;
import com.example.demo.domain.request.OtpRequest;
import com.example.demo.domain.request.ReqForgetPassword;
import com.example.demo.domain.request.ReqLoginDto;
import com.example.demo.domain.request.ReqSignupDto;
import com.example.demo.domain.response.ResLoginDto;
import com.example.demo.domain.response.ResSignupDto;
import com.example.demo.service.EmailService;
import com.example.demo.service.SmsService;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.error.InvalidException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@PropertySource("classpath:application-local.properties")
public class AuthController {
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SmsService smsService;
    private final RedisTemplate<String, String> redisTemplate;
    public AuthController(
        UserService userService, PasswordEncoder passwordEncoder, SecurityUtil securityUtil,
        AuthenticationManagerBuilder authenticationManagerBuilder, EmailService emailService, SmsService smsService,
        RedisTemplate<String, String> redisTemplate){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.securityUtil = securityUtil;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.emailService = emailService;
        this.smsService = smsService;
        this.redisTemplate = redisTemplate;
    }
    @Value("${nam.jwt.base64-secret}")
    private String jwtKey;
    @Value("${nam.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpitration;
    @Value("${nam.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;
    @Value("${google.client-id}")
    private String googleClientId;

    @PostMapping("/signup")
    public ResponseEntity<ResSignupDto> signupPage(@RequestBody ReqSignupDto reqSignup) throws InvalidException{
        String check_email = reqSignup.getEmail();
        if(this.userService.isEmailExistsInDB(check_email)){
            throw new InvalidException("Email has already existed");
        }
        reqSignup.setPassword(this.passwordEncoder.encode(reqSignup.getPassword()));
        User new_user = this.userService.convertReqSignupToUser(reqSignup);
        this.userService.createNewUser(new_user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertUserToResSignupDto(new_user));
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDto> loginPage(@Valid @RequestBody ReqLoginDto loginDto){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword());

        // xác thực người dùng => khi gọi authenticate thì nó sẽ tìm đến loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        // set thông tin người dùng đăng nhập vào context (có thể sử dụng sau này)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDto resLoginDto = new ResLoginDto();
        User currentUserDB = this.userService.fetchUserByEmail(loginDto.getUsername());
        if (currentUserDB != null){
            ResLoginDto.UserLogin userLogin = new ResLoginDto.UserLogin();
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setId(currentUserDB.getId());
            userLogin.setName(currentUserDB.getName());
            resLoginDto.setUser(userLogin);
        }
        String access_token = this.securityUtil.createAccessToken(currentUserDB.getEmail(), resLoginDto);
        resLoginDto.setAccessToken(access_token);

        String refresh_token = this.securityUtil.createRefreshToken(currentUserDB.getEmail(), resLoginDto);

        //lưu refresh_token vào database của User
        this.userService.updateRefreshToken(refresh_token, currentUserDB.getEmail());
        //
        ResponseCookie responseCookie =  ResponseCookie
            .from("refresh_token", refresh_token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(accessTokenExpitration)
            .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(resLoginDto);

    }

    @PostMapping("/google")
    public ResponseEntity<ResLoginDto> googlePage (@RequestBody Map<String, String> token) throws GeneralSecurityException, IOException {
        String tokenGoogle = token.get("token");
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
            .setAudience(Collections.singletonList(googleClientId))
            .build();
        GoogleIdToken idToken = verifier.verify(tokenGoogle);
        ResLoginDto resLoginDto = new ResLoginDto();
        ResLoginDto.UserLogin userLogin = new ResLoginDto.UserLogin();
        if (idToken != null){
            Payload payload = idToken.getPayload();
            userLogin.setEmail(payload.getEmail());
            userLogin.setName((String) payload.get("name"));
        }
        resLoginDto.setUser(userLogin);
        String access_token = this.securityUtil.createAccessToken(userLogin.getEmail(), resLoginDto);
        String refresh_token = this.securityUtil.createRefreshToken(userLogin.getEmail(), resLoginDto);
        if (!this.userService.isEmailExistsInDB(userLogin.getEmail())){
            User new_user = new User();
                new_user.setEmail(userLogin.getEmail());
                new_user.setName(userLogin.getName());
                new_user.setRefreshToken(refresh_token);
            this.userService.createNewUser(new_user);
        }

        this.userService.updateRefreshToken(refresh_token, userLogin.getEmail());


        resLoginDto.setAccessToken(access_token);

        ResponseCookie responseCookie =  ResponseCookie
            .from("refresh_token", refresh_token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(accessTokenExpitration)
            .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(resLoginDto);
    }

    @PostMapping("/forget")
    public ResponseEntity<String> forgetPassword(@RequestBody Map<String, String> user_email) throws InvalidException {
        String email = user_email.get("email");
        User current_user = this.userService.fetchUserByEmail(email);
        if (current_user == null){
            throw new InvalidException("Email is invalid");
        }
        String otp = this.smsService.generateOtp();
        this.redisTemplate.opsForValue().set(email, otp, Duration.ofMinutes(3));
        this.emailService.sendVerificationEmail(email, "otp:",otp);

        return ResponseEntity.ok().body("Send message successful");
    }

    @PostMapping("/forget/check")
    public ResponseEntity<String> postMethodName(@RequestBody OtpRequest otpRequest) throws InvalidException {
        String current_email = otpRequest.getEmail();
        if (current_email != null){
            String sys_otp = this.redisTemplate.opsForValue().get(current_email);
            if (otpRequest.getOtp() == sys_otp){
                return ResponseEntity.ok().body("Successfull");
            }
            else{
                return ResponseEntity.ok().body("Try again");
            }
        }
        else{
            throw new InvalidException("Email is invalid");
        }
    }
    @PutMapping("/forget/check/password")
    public ResponseEntity<String> updateForgetPassword(@RequestBody ReqForgetPassword  request) throws InvalidException {
        User updateUser = this.userService.fetchUserByEmail(request.getEmail());
        if (updateUser != null){
            updateUser.setPassword(this.passwordEncoder.encode(updateUser.getPassword()));
            this.userService.createNewUser(updateUser);
            return ResponseEntity.ok().body("Password was update successfully");
        }
        else{
            throw new InvalidException("Email is invalid");
        }
    }
    @GetMapping("/account")
    public ResponseEntity<ResLoginDto.UserLogin> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        User currentUserDB = this.userService.fetchUserByEmail(email);
        ResLoginDto.UserLogin userLogin = new ResLoginDto.UserLogin();

        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
        }

        return ResponseEntity.ok().body(userLogin);
    }

    @GetMapping("/refresh")
    public ResponseEntity<ResLoginDto> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token) throws InvalidException {
        if (refresh_token.equals("abc")) {
            throw new InvalidException("Bạn không có refresh token ở cookie");
        }
        // check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        // check user by token + email
        User currentUser = this.userService.fecthUserByEmailAndRefreshToken(email, refresh_token);
        if (currentUser == null) {
            throw new InvalidException("Refresh Token không hợp lệ");
        }

        // issue new token/set refresh token as cookies
        ResLoginDto res = new ResLoginDto();
        User currentUserDB = this.userService.fetchUserByEmail(email);
        if (currentUserDB != null) {
            ResLoginDto.UserLogin userLogin = new ResLoginDto.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName());
            res.setUser(userLogin);
        }

        // create access token
        String access_token = this.securityUtil.createAccessToken(email, res);
        res.setAccessToken(access_token);

        // create refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

        // update user
        this.userService.updateRefreshToken(new_refresh_token, email);

        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout() throws InvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        if (email.equals("")) {
            throw new InvalidException("Access Token không hợp lệ");
        }
        this.userService.updateRefreshToken(email, email);

        // remove refresh token cookie
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(null);
    }

}
