package com.HTTTDL.Backend.model;

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
    Position position;
}
