package ru.merezh.authservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users_auth")
public class UserAuth {

    @Id
    private Long id;

    @Column(nullable = false)
    private String tokenHash;
}
