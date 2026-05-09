package edu.cit.olodin.feature.auth;

import lombok.Getter;

@Getter
public class AuthResponse {
    private String token;
    private String role;
    private boolean isNewUser;

    public AuthResponse(String token, String role, boolean isNewUser) {
        this.token = token;
        this.role = role;
        this.isNewUser = isNewUser;
    }
}
