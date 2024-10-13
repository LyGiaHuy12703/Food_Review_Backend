package com.HTTTDL.Backend.model;

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
    @JoinColumn(name = "user_id")
    User user;
    @ManyToOne
    @JoinColumn(name = "position_id")
    Position position;
}
