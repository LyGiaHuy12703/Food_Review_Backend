package com.HTTTDL.Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "geo_features")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeoFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
    String address;
    String phone;
    LocalTime open;
    LocalTime close;
    String advantage;
    String disadvantage;
    Float rate;

    @Column(columnDefinition = "POINT")
    @JsonIgnore
    Point point;

    @OneToMany(mappedBy = "geoFeatures", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<Review> reviews = new ArrayList<>();
    @ElementCollection
    Set<String> images = new HashSet<>();

}
