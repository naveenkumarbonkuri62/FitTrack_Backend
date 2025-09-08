// src/main/java/com/FitTrack/service/DietPlanService.java
package com.FitTrack.service;

import com.FitTrack.model.DietPlan;
import com.FitTrack.model.User;
import com.FitTrack.repository.DietPlanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DietPlanService {

    private final DietPlanRepository dietPlanRepository;

    public DietPlanService(DietPlanRepository dietPlanRepository) {
        this.dietPlanRepository = dietPlanRepository;
    }

    // Log a meal for a user
    public DietPlan logMeal(User user, String mealType, String description, Integer calories) {
        DietPlan meal = DietPlan.builder()
                .user(user)
                .mealType(mealType)
                .description(description)
                .calories(calories)
                .timestamp(LocalDateTime.now())
                .build();
        return dietPlanRepository.save(meal);
    }

    // Get all meals of a user
    public List<DietPlan> getUserMeals(User user) {
        return dietPlanRepository.findByUser(user);
    }
}
