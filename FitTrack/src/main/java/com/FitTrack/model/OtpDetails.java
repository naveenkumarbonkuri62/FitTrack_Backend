package com.FitTrack.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "otp_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String otp;
    private String expiryTime; // store as timestamp string
}
