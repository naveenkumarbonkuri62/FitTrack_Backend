// src/main/java/com/FitTrack/model/DietPlan.java
package com.FitTrack.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mealType; // Breakfast, Lunch, Dinner, Snack
    private String description; // e.g., "Oatmeal with fruits"
    private Integer calories; // calories consumed
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
