package edu.cit.olodin.feature.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {
    private String firstname;
    private String lastname;
}