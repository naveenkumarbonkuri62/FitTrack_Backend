package com.FitTrack.service;

import com.FitTrack.model.ProgressLog;
import com.FitTrack.model.User;
import com.FitTrack.repository.ProgressLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProgressService {

    @Autowired
    private ProgressLogRepository progressLogRepository;

    public ProgressLog logProgress(ProgressLog log) {
        return progressLogRepository.save(log);
    }

    public List<ProgressLog> getProgress(User user) {
        return progressLogRepository.findByUserOrderByDateAsc(user);
    }
}
