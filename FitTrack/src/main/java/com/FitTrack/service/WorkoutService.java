package com.FitTrack.service;

import com.FitTrack.model.User;
import com.FitTrack.model.Workout;
import com.FitTrack.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;

    public WorkoutService(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    // Log a new workout
    public Workout logWorkout(User user, String type, Integer duration, Integer calories) {
        Workout workout = Workout.builder()
                .user(user)
                .type(type)
                .duration(duration)
                .calories(calories)
                .timestamp(LocalDateTime.now()) // ✅ auto timestamp
                .build();
        return workoutRepository.save(workout);
    }

    // Get all workouts of a user (latest first)
    public List<Workout> getUserWorkouts(User user) {
        return workoutRepository.findByUserOrderByTimestampDesc(user);
    }
}
