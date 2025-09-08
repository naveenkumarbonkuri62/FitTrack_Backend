package com.FitTrack.controller;

import com.FitTrack.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * Get analytics data for a user by userId
     * @param userId The ID of the user
     * @return JSON containing calories, progress, and workout type distribution
     */
    @GetMapping("/user/{userId}")
    public Map<String, Object> getUserAnalytics(@PathVariable Long userId) {
        return analyticsService.getUserAnalytics(userId);
    }
}
