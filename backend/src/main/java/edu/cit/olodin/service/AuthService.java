package edu.cit.olodin.service;

import edu.cit.olodin.entity.User;
import edu.cit.olodin.exception.AuthException;
import edu.cit.olodin.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}