package com.FitTrack.controller;

import com.FitTrack.model.ProgressLog;
import com.FitTrack.model.User;
import com.FitTrack.service.ProgressService;
import com.FitTrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/log/{userId}")
    public ResponseEntity<ProgressLog> logProgress(@PathVariable Long userId, @RequestBody ProgressLog log) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        log.setUser(user);
        return ResponseEntity.ok(progressService.logProgress(log));
    }

    @GetMapping("/view/{userId}")
    public ResponseEntity<List<ProgressLog>> viewProgress(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<ProgressLog> logs = progressService.getProgress(user);
        return ResponseEntity.ok(logs);
    }
}
