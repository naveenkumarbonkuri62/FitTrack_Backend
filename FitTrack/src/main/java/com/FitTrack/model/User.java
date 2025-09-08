package com.FitTrack.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String role;   // ROLE_USER or ROLE_ADMIN

    @Column(nullable = false)
    private String password; // hashed password

    @Column(nullable = false)
    private Boolean isVerified = false;

    private Integer age;
    private String gender;
    private Double height;
    private Double weight;

    private String createdAt;
    private String updatedAt;

    // --- Relationships ---
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Workout> workouts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietPlan> dietPlans;
}
