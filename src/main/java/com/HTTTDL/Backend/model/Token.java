package com.HTTTDL.Backend.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column(length = 500)
    String token;
    @Column(length = 500)
    String refreshToken;
    Date createAt;
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn( name = "user_id")
    private User user;
}
