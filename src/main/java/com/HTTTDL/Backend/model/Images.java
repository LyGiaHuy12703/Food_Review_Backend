package com.HTTTDL.Backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Images {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String url;
    @ManyToOne
    @JoinColumn(name = "position_id")
    @JsonBackReference
    GeoFeature geoFeatures;
}
