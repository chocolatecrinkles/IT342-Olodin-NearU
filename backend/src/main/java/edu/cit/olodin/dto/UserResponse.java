package edu.cit.olodin.dto;

public class UserResponse {

    public Long id;
    public String firstname;
    public String lastname;
    public String email;
    public String role;

    public UserResponse() {}

    public UserResponse(
            Long id,
            String firstname,
            String lastname,
            String email,
            String role
    ) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
    }
}