package edu.cit.olodin.feature.auth;

import edu.cit.olodin.feature.user.Role;
import edu.cit.olodin.feature.user.User;
import edu.cit.olodin.exception.AuthException;
import edu.cit.olodin.feature.user.UserRepository;
import edu.cit.olodin.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Collections;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${google.client.id}")
    private String googleClientId;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User register(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AuthException("Email already registered", "AUTH_EMAIL_EXISTS");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found", "AUTH_USER_NOT_FOUND"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException("Incorrect password", "AUTH_INVALID_PASSWORD");
        }

        return user;
    }

    public AuthResponse googleLogin(String token) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new JacksonFactory()
            ).setAudience(Collections.singletonList(googleClientId)).build();

            GoogleIdToken idToken = verifier.verify(token);

            if (idToken == null) {
                throw new AuthException("Invalid Google token", "AUTH_GOOGLE_INVALID");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");
            User user = userRepository.findByEmail(email).orElse(null);

            boolean isNewUser = false;

            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setFirstname(firstName != null ? firstName : "Google");
                user.setLastname(lastName != null ? lastName : "User");
                user.setPassword(passwordEncoder.encode("GOOGLE_USER"));

                user.setRole(Role.STUDENT);

                user = userRepository.save(user);
                isNewUser = true;
            }

            String jwt = jwtUtil.generateToken(user.getEmail(), user.getRole());

            System.out.println("IS NEW USER: " + isNewUser);
            System.out.println("EMAIL: " + email);

            return new AuthResponse(jwt, user.getRole().name(), isNewUser);

        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthException("Google authentication failed", "AUTH_GOOGLE_ERROR");
        }
    }
}