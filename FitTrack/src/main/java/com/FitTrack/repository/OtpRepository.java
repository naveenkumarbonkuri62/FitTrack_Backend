package com.FitTrack.repository;

import com.FitTrack.model.OtpDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpDetails, Long> {
    Optional<OtpDetails> findByEmail(String email);
}
