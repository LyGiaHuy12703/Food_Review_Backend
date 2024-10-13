package com.HTTTDL.Backend.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String name;
    String username;
    String password;
    Set<String> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Review> reviews = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    Token token;
}
