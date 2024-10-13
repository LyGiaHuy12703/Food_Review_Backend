package com.HTTTDL.Backend.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String name;
    @Column(columnDefinition = "POINT")
    Point positions;
    String address;
    String phone;
    LocalTime open;
    LocalTime close;
    String advantage;
    String disadvantage;
    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Review> reviews = new ArrayList<>();
    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Images> images = new ArrayList<>();
}
