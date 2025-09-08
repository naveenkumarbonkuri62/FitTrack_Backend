package com.FitTrack.repository;

import com.FitTrack.model.ProgressLog;
import com.FitTrack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProgressLogRepository extends JpaRepository<ProgressLog, Long> {

    // JPA can now resolve this method using the 'user' field
    List<ProgressLog> findByUserOrderByDateAsc(User user);
}
