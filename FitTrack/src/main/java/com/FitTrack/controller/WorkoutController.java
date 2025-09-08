package com.FitTrack.controller;

import com.FitTrack.model.User;
import com.FitTrack.model.Workout;
import com.FitTrack.repository.UserRepository;
import com.FitTrack.service.WorkoutService;
import com.FitTrack.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/workout")
public class WorkoutController {

    @Autowired
    private WorkoutService workoutService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ DTO to avoid exposing user data & lazy loading issues
    public record WorkoutDTO(Long id, String type, Integer duration, Integer calories, String timestamp) {}

    // --- Log a workout ---
    @PostMapping("/log")
    public Map<String, Object> logWorkout(@RequestHeader("Authorization") String token,
                                          @RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();

        // Remove "Bearer " prefix and extract email from JWT
        String email = jwtUtil.extractEmail(token.substring(7));
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            response.put("status", "error");
            response.put("message", "User not found!");
            return response;
        }

        // ✅ Safe type casting (avoid ClassCastException)
        String type = (String) payload.get("type");
        Integer duration = payload.get("duration") != null ? ((Number) payload.get("duration")).intValue() : 0;
        Integer calories = payload.get("calories") != null ? ((Number) payload.get("calories")).intValue() : 0;

        Workout workout = workoutService.logWorkout(user, type, duration, calories);

        response.put("status", "success");
        response.put("message", "Workout logged successfully!");
        response.put("workout", new WorkoutDTO(
                workout.getId(),
                workout.getType(),
                workout.getDuration(),
                workout.getCalories(),
                workout.getTimestamp().toString()
        ));
        return response;
    }

    // --- Get all workouts of a user ---
    @GetMapping("/list")
    public List<WorkoutDTO> getUserWorkouts(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.substring(7));
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return List.of();

        return workoutService.getUserWorkouts(user)
                .stream()
                .map(workout -> new WorkoutDTO(
                        workout.getId(),
                        workout.getType(),
                        workout.getDuration(),
                        workout.getCalories(),
                        workout.getTimestamp().toString()
                ))
                .collect(Collectors.toList());
    }
}
