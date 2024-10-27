package com.HTTTDL.Backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
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
}
