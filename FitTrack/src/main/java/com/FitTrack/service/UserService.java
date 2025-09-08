package com.FitTrack.service;

import com.FitTrack.model.User;
import com.FitTrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public Optional<User> getUserById(Long id) {
        return userRepo.findById(id);
    }
}
