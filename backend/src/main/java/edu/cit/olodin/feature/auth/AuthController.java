package edu.cit.olodin.feature.auth;

import edu.cit.olodin.feature.user.UserResponse;
import edu.cit.olodin.feature.user.Role;
import edu.cit.olodin.feature.user.User;
import edu.cit.olodin.exception.AuthException;
import edu.cit.olodin.feature.user.UserRepository;
import edu.cit.olodin.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found", "AUTH_USER_NOT_FOUND"));
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = authService.login(request.getEmail(), request.getPassword());
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return ResponseEntity.ok(Map.of(
                "token", token
        ));
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
        return ResponseEntity.ok(authService.googleLogin(request.token));
    }

    @PostMapping("/set-role")
    public ResponseEntity<?> setRole(@RequestBody Map<String, String> body) {
        User user = getCurrentUser();

        Role role = Role.valueOf(body.get("role"));
        user.setRole(role);

        userRepository.save(user);

        String jwt = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return ResponseEntity.ok(new AuthResponse(jwt, role.name(), false));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUserInfo() {

        User user = getCurrentUser();

        UserResponse response = new UserResponse(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getRole().name()
        );

        return ResponseEntity.ok(response);
    }
}