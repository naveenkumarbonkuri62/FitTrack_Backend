// src/main/java/com/FitTrack/repository/DietPlanRepository.java
package com.FitTrack.repository;

import com.FitTrack.model.DietPlan;
import com.FitTrack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {
    List<DietPlan> findByUser(User user);
    List<DietPlan> findByUserAndTimestampBetween(User user, LocalDateTime start, LocalDateTime end);
}
