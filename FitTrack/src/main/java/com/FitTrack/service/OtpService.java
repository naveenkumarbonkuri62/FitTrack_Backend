package com.FitTrack.service;

import com.FitTrack.model.OtpDetails;
import com.FitTrack.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    public String generateOtp(String email) {
        // 6-digit random OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Set expiry time (5 mins)
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);
        String expiryTime = expiry.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Save OTP in DB
        OtpDetails otpDetails = otpRepository.findByEmail(email)
                .orElse(new OtpDetails());
        otpDetails.setEmail(email);
        otpDetails.setOtp(otp);
        otpDetails.setExpiryTime(expiryTime);
        otpRepository.save(otpDetails);

        // Send Email
        emailService.sendOtpEmail(email, otp);

        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        Optional<OtpDetails> otpRecord = otpRepository.findByEmail(email);

        if (otpRecord.isEmpty()) return false;

        OtpDetails details = otpRecord.get();

        // Check expiry
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = LocalDateTime.parse(details.getExpiryTime(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (now.isAfter(expiry)) {
            return false; // OTP expired
        }

        return details.getOtp().equals(otp);
    }
}
