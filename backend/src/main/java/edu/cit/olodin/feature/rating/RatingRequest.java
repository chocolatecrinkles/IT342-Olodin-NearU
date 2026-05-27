package edu.cit.olodin.feature.rating;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingRequest {
    private Integer rating;
    private String comment;
}