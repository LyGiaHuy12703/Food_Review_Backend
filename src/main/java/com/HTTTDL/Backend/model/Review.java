package com.HTTTDL.Backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String comment;
    int rating;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id")
    User user;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "position_id")
    GeoFeature geoFeatures;

    LocalDateTime createdAt = LocalDateTime.now();

    void onCreate(){
        this.createdAt = LocalDateTime.now();
    }
}
