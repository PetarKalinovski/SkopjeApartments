package com.example.proekt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Advertisement{

    @Id
    @GeneratedValue
    private Long id;

    @ElementCollection
    private List<Double> ratings;

    @ElementCollection
    private List<String> comments;

    @Enumerated(EnumType.STRING)
    private AdvertisementType type;

    private Double price;

    @ManyToOne
    private Apartment apartment;

    public Advertisement() {
    }

    public Advertisement(Apartment apartment, AdvertisementType type, Double price) {
        this.apartment = apartment;
        this.type=type;
        this.price=price;
    }
}

