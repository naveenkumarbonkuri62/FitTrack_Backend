package com.FitTrack.controller;

import com.FitTrack.model.DietPlan;
import com.FitTrack.model.User;
import com.FitTrack.model.Workout;
import com.FitTrack.service.DietPlanService;
import com.FitTrack.repository.UserRepository;
import com.FitTrack.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/diet")
@CrossOrigin(origins = "http://localhost:5173")
public class DietPlanController {

    @Autowired
    private DietPlanService dietPlanService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // --- DTO for frontend ---
    public record DietPlanDTO(Long id, String mealType, String description, Integer calories, String timestamp) {}

    public record DailyCalorieSummaryDTO(Integer totalCaloriesConsumed, Integer totalCaloriesBurned, Integer netCalories) {}

    // --- Log a meal ---
    @PostMapping("/add")
    public Map<String, Object> logMeal(@RequestHeader("Authorization") String token,
                                       @RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();

        String email = jwtUtil.extractEmail(token.substring(7));
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            response.put("status", "error");
            response.put("message", "User not found!");
            return response;
        }

        String mealType = Objects.toString(payload.get("mealType"), "");
        String description = Objects.toString(payload.get("description"), "");
        Integer calories = payload.get("calories") != null ? ((Number) payload.get("calories")).intValue() : 0;

        DietPlan meal = dietPlanService.logMeal(user, mealType, description, calories);

        response.put("status", "success");
        response.put("message", "Meal logged successfully!");
        response.put("meal", new DietPlanDTO(
                meal.getId(),
                meal.getMealType(),
                meal.getDescription(),
                meal.getCalories(),
                meal.getTimestamp().toString()
        ));

        return response;
    }

    // --- Get all meals of a user ---
    @GetMapping("/list")
    public List<DietPlanDTO> getMeals(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.substring(7));
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return List.of();

        return dietPlanService.getUserMeals(user)
                .stream()
                .map(meal -> new DietPlanDTO(
                        meal.getId(),
                        meal.getMealType(),
                        meal.getDescription(),
                        meal.getCalories(),
                        meal.getTimestamp().toString()
                ))
                .collect(Collectors.toList());
    }

    // --- Daily calorie summary ---
    @GetMapping("/summary/daily")
    public DailyCalorieSummaryDTO getDailySummary(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.substring(7));
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return new DailyCalorieSummaryDTO(0, 0, 0);

        LocalDate today = LocalDate.now();

        int totalConsumed = dietPlanService.getUserMeals(user).stream()
                .filter(meal -> meal.getTimestamp().toLocalDate().equals(today))
                .mapToInt(DietPlan::getCalories)
                .sum();

        int totalBurned = Optional.ofNullable(user.getWorkouts()).orElse(List.of()).stream()
                .filter(workout -> workout.getTimestamp().toLocalDate().equals(today))
                .mapToInt(workout -> workout.getCalories() != null ? workout.getCalories() : 0)
                .sum();

        int netCalories = totalConsumed - totalBurned;

        return new DailyCalorieSummaryDTO(totalConsumed, totalBurned, netCalories);
    }

    // --- Daily calorie timeline (hourly) ---
    @GetMapping("/summary/timeline")
    public List<Map<String, Object>> getDailyTimeline(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.substring(7));
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return List.of();

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        List<DietPlan> mealsToday = dietPlanService.getUserMeals(user).stream()
                .filter(meal -> !meal.getTimestamp().isBefore(startOfDay) && !meal.getTimestamp().isAfter(endOfDay))
                .collect(Collectors.toList());

        List<Workout> workoutsToday = Optional.ofNullable(user.getWorkouts()).orElse(List.of()).stream()
                .filter(workout -> !workout.getTimestamp().isBefore(startOfDay) && !workout.getTimestamp().isAfter(endOfDay))
                .collect(Collectors.toList());

        Map<Integer, Integer> consumedByHour = new HashMap<>();
        Map<Integer, Integer> burnedByHour = new HashMap<>();
        for (int h = 0; h < 24; h++) {
            consumedByHour.put(h, 0);
            burnedByHour.put(h, 0);
        }

        mealsToday.forEach(meal -> consumedByHour.put(meal.getTimestamp().getHour(),
                consumedByHour.get(meal.getTimestamp().getHour()) + meal.getCalories()));

        workoutsToday.forEach(workout -> burnedByHour.put(workout.getTimestamp().getHour(),
                burnedByHour.get(workout.getTimestamp().getHour()) + (workout.getCalories() != null ? workout.getCalories() : 0)));

        List<Map<String, Object>> timeline = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("hour", h);
            entry.put("consumed", consumedByHour.get(h));
            entry.put("burned", burnedByHour.get(h));
            timeline.add(entry);
        }

        return timeline;
    }
    // src/main/java/com/FitTrack/controller/DietPlanController.java
    @GetMapping("/summary/today-hourly")
    public Map<String, Object> getHourlySummary(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.substring(7));
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return Map.of("hours", List.of(), "consumed", List.of(), "burned", List.of(), "predicted", List.of());

        // Prepare arrays
        List<Integer> consumed = new ArrayList<>();
        List<Integer> burned = new ArrayList<>();
        List<Integer> predicted = new ArrayList<>();
        List<String> hours = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        for (int h = 0; h < 24; h++) {
            final int hour = h;
            int consumedHour = dietPlanService.getUserMeals(user).stream()
                    .filter(meal -> meal.getTimestamp().getHour() == hour
                            && meal.getTimestamp().toLocalDate().equals(now.toLocalDate()))
                    .mapToInt(DietPlan::getCalories).sum();
            int burnedHour = user.getWorkouts().stream()
                    .filter(workout -> workout.getTimestamp().getHour() == hour
                            && workout.getTimestamp().toLocalDate().equals(now.toLocalDate()))
                    .mapToInt(w -> w.getCalories() != null ? w.getCalories() : 0).sum();

            consumed.add(consumedHour);
            burned.add(burnedHour);
            // Simple prediction: average calories consumed per past hour * remaining hours
            int predictedHour = (hour < now.getHour()) ? consumedHour : consumedHour; // placeholder for smarter prediction
            predicted.add(predictedHour);

            hours.add(String.format("%02d:00", hour));
        }

        return Map.of("hours", hours, "consumed", consumed, "burned", burned, "predicted", predicted);
    }

}
