package com.earthlog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "badges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    @Column(length = 255)
    private String criteria;

    @ManyToMany(mappedBy = "badges")
    @Builder.Default
    private Set<User> users = new HashSet<>();
}
