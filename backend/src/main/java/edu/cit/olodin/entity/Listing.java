package edu.cit.olodin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "listings")
@Getter
@Setter
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type listingType;

    @Column(nullable = false)
    private String address;

    private Double price;

    private Double latitude;
    private Double longitude;

    private String description;

    @Column(nullable = false)
    private Long ownerId;
}
