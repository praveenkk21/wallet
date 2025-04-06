package com.WalletProject.WalletProject.controller;

import com.WalletProject.WalletProject.model.User;
import com.WalletProject.WalletProject.model.UserType;
import com.WalletProject.WalletProject.repository.UserRepo;
import com.WalletProject.WalletProject.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth/github")
public class GitHubAuthController {

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    private final UserRepo userRepository;
    private final JwtService jwtService;

    public GitHubAuthController(UserRepo userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/login")
    public ResponseEntity<String> redirectToGitHub() {
        URI uri = URI.create("https://github.com/login/oauth/authorize?client_id=" + clientId + "&scope=user:email");
        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }

    @GetMapping("/callback")
    public ResponseEntity<?> githubAuthCallback(@RequestParam("code") String code) { // Changed return type and removed HttpServletResponse
        RestTemplate restTemplate = new RestTemplate();

        // Exchange authorization code for access token
        String tokenUrl = "https://github.com/login/oauth/access_token?client_id=" + clientId
                + "&client_secret=" + clientSecret + "&code=" + code;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> tokenResponse = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, Map.class);

        if (tokenResponse.getBody() == null || !tokenResponse.getBody().containsKey("access_token")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "github_token_exchange_failed", "message", "Failed to exchange authorization code for access token."));
        }

        String accessToken = tokenResponse.getBody().get("access_token").toString();

        // Fetch GitHub user details
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> authEntity = new HttpEntity<>(authHeaders);

        ResponseEntity<Map> userResponse = restTemplate.exchange("https://api.github.com/user", HttpMethod.GET, authEntity, Map.class);

        if (userResponse.getBody() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "github_user_fetch_failed", "message", "Failed to fetch user details from GitHub."));
        }

        String githubUsername = userResponse.getBody().get("login").toString();
        String githubEmail = userResponse.getBody().get("email") != null ? userResponse.getBody().get("email").toString() : githubUsername + "@github.noreply.com";

        // Check if user exists in DB by GitHub Username
        Optional<User> existingUserByUsername = userRepository.findByName(githubUsername);
        if (existingUserByUsername.isPresent()) {
            // Generate JWT and return it
            String jwtToken = jwtService.generateToken(existingUserByUsername.get().getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "duplicate_github_username", "message", "An account with this GitHub username already exists.", "token", jwtToken));
        }

        // Check if user exists in DB by Email
        Optional<User> existingUserByEmail = Optional.ofNullable(userRepository.findByEmail(githubEmail));
        if (existingUserByEmail.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "duplicate_email", "message", "An account with this email already exists. Please log in with your existing credentials or contact support to link your GitHub account."));
        }

        // Register new user
        User user = new User();
        user.setName(githubUsername);
        user.setEmail(githubEmail);
        user.setAuthority("USER");
        user.setContactNo("1234567890");
        user.setPassword(githubUsername);
        user.setIsEnabled(true);
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(true);
        user.setIsCredentialsNonExpired(true);
        userRepository.save(user);

        // Generate JWT and return it
        String jwtToken = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(Map.of("token", jwtToken, "username", githubUsername));
    }
}
