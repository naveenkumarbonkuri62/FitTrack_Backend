package com.FitTrack.controller;

import com.FitTrack.model.User;
import com.FitTrack.repository.UserRepository;
import com.FitTrack.service.OtpService;
import com.FitTrack.config.JwtUtil;
import com.FitTrack.dto.UserRequest;
import com.FitTrack.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173") // allows React frontend
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Step 1: Register user & send OTP
    @PostMapping("/register")
    public Map<String, Object> registerUser(@RequestBody UserRequest userRequest) {
        Map<String, Object> response = new HashMap<>();

        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            response.put("status", "error");
            response.put("message", "User already exists!");
            return response;
        }

        User user = User.builder()
                .email(userRequest.getEmail())
                .fullName(userRequest.getFullName())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .role(userRequest.getRole() != null ? userRequest.getRole().toUpperCase() : "ROLE_USER")
                .age(userRequest.getAge())
                .gender(userRequest.getGender())
                .height(userRequest.getHeight())
                .weight(userRequest.getWeight())
                .isVerified(false)
                .createdAt(LocalDateTime.now().toString())
                .updatedAt(LocalDateTime.now().toString())
                .build();

        userRepository.save(user);
        otpService.generateOtp(user.getEmail()); // send OTP

        response.put("status", "success");
        response.put("message", "Registration successful! OTP sent to your email.");
        return response;
    }

    // Step 2: Verify OTP
    @PostMapping("/verify-otp")
    public Map<String, Object> verifyOtp(@RequestParam String email,
                                         @RequestParam String otp) {
        Map<String, Object> response = new HashMap<>();

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            response.put("status", "error");
            response.put("message", "User not found!");
            return response;
        }

        if (!otpService.verifyOtp(email, otp)) {
            response.put("status", "error");
            response.put("message", "Invalid or expired OTP!");
            return response;
        }

        user.setIsVerified(true);
        userRepository.save(user);

        response.put("status", "success");
        response.put("message", "Email verified successfully!");
        return response;
    }

    // Step 3: Login (JSON body)
    @PostMapping("/login")
    public Map<String, Object> loginUser(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();

        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);
        if (user == null) {
            response.put("status", "error");
            response.put("message", "User not found!");
            return response;
        }

        // Skip email verification check for admin
        if (!"ROLE_ADMIN".equals(user.getRole()) && !user.getIsVerified()) {
            response.put("status", "error");
            response.put("message", "Please verify your email first!");
            return response;
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            response.put("status", "error");
            response.put("message", "Invalid password!");
            return response;
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        response.put("status", "success");
        response.put("message", "Login successful!");
        response.put("token", token);
        response.put("role", user.getRole());
        response.put("fullName", user.getFullName()); // include full name for frontend

        return response;
    }

    // Step 4: Resend OTP
    @PostMapping("/resend-otp")
    public Map<String, Object> resendOtp(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            response.put("status", "error");
            response.put("message", "User not found!");
            return response;
        }

        if (user.getIsVerified()) {
            response.put("status", "error");
            response.put("message", "User already verified!");
            return response;
        }

        otpService.generateOtp(email);

        response.put("status", "success");
        response.put("message", "OTP resent successfully to your email!");
        return response;
    }
}
