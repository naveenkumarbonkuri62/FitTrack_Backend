package com.FitTrack.dto;

import lombok.Data;

@Data
public class UserRequest {
    private String email;
    private String fullName;
    private String password;   // NEW: required for registration
    private String role;       // optional: default ROLE_USER
    private Integer age;
    private String gender;
    private Double height;
    private Double weight;
}
