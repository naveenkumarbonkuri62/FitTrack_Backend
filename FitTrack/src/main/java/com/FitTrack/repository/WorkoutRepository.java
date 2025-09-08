package com.FitTrack.repository;

import com.FitTrack.model.User;
import com.FitTrack.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByUser(User user);
    List<Workout> findByUserOrderByTimestampDesc(User user); // ✅ new
}
