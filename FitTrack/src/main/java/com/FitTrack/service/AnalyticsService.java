package com.FitTrack.service;

import com.FitTrack.model.DietPlan;
import com.FitTrack.model.ProgressLog;
import com.FitTrack.model.User;
import com.FitTrack.model.Workout;
import com.FitTrack.repository.DietPlanRepository;
import com.FitTrack.repository.ProgressLogRepository;
import com.FitTrack.repository.UserRepository;
import com.FitTrack.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final WorkoutRepository workoutRepo;
    private final DietPlanRepository dietRepo;
    private final ProgressLogRepository progressRepo;
    private final UserRepository userRepo;

    public Map<String, Object> getUserAnalytics(Long userId) {
        Map<String, Object> analytics = new HashMap<>();

        // ✅ Fetch User object
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Fetch Diet Plans and Workouts
        List<DietPlan> diets = dietRepo.findByUser(user);
        List<Workout> workouts = workoutRepo.findByUser(user);

        // --- Calories consumed vs burned ---
        Map<String, Map<String, Integer>> caloriesMap = new TreeMap<>();
        for (DietPlan diet : diets) {
            String date = diet.getTimestamp().toLocalDate().toString();
            caloriesMap.putIfAbsent(date, new HashMap<>());
            caloriesMap.get(date).put("consumed", diet.getCalories());
        }
        for (Workout workout : workouts) {
            String date = workout.getTimestamp().toLocalDate().toString();
            caloriesMap.putIfAbsent(date, new HashMap<>());
            caloriesMap.get(date).put("burned", workout.getCalories());
        }
        analytics.put("calories", caloriesMap);

        // --- Workout type distribution ---
        Map<String, Long> workoutTypeDist = workouts.stream()
                .collect(Collectors.groupingBy(Workout::getType, Collectors.counting()));
        analytics.put("workoutTypeDistribution", workoutTypeDist);

        // --- Progress logs over time ---
        List<ProgressLog> progressLogs = progressRepo.findByUserOrderByDateAsc(user);
        analytics.put("progressLogs", progressLogs);

        return analytics;
    }
}
