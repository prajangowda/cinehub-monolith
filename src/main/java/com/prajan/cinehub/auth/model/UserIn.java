package com.prajan.cinehub.auth.model;

import com.prajan.cinehub.auth.TheatreOwnerRequest.TheatreOwnerRequest;
import com.prajan.cinehub.auth.enums.Role;
import com.prajan.cinehub.auth.enums.provider;
import com.prajan.cinehub.theatre.entity.Theatre;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String name;

    private String password;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private provider provider;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(nullable = false)
    private boolean active = true;


    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TheatreOwnerRequest> theatreOwnerRequests = new ArrayList<>();

    @Override
    public String toString() {
        return "UserIn{" +
                "email='" + email + '\'' +
                ", provider=" + provider +
                ", role=" + role +
                '}';
    }
}
