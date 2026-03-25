package com.disastermanagement.emergency_alert_service.service;

import com.disastermanagement.emergency_alert_service.dto.*;
import com.disastermanagement.emergency_alert_service.entity.RefreshToken;
import com.disastermanagement.emergency_alert_service.entity.Role;
import com.disastermanagement.emergency_alert_service.entity.User;
import com.disastermanagement.emergency_alert_service.repository.RefreshTokenRepository;
import com.disastermanagement.emergency_alert_service.repository.UserRepository;
import com.disastermanagement.emergency_alert_service.utility.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepo;

    // constructor injection
    public AuthService(UserRepository userRepo,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenRepository refreshTokenRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenRepo = refreshTokenRepo;
    }


    public AuthResponse login(AuthRequest request) {

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow();

        if (!passwordEncoder.matches(
                request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        RefreshToken token = refreshTokenRepo.findByUser(user).orElse(new RefreshToken()); // If not found, create new instance
        token.setUser(user);
        token.setToken(refreshToken);
        token.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshTokenRepo.save(token);

        return new AuthResponse(accessToken, refreshToken, user.getRole().name());
    }
    public AuthResponse register(AuthRequest request) {

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());
        userRepo.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {

        RefreshToken token = refreshTokenRepo.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        User user = token.getUser();

        String newAccessToken = jwtService.generateAccessToken(user);

        return new AuthResponse(newAccessToken, refreshToken);
    }
    @Transactional
    public void logout(String refreshToken) {

        RefreshToken token = refreshTokenRepo.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        refreshTokenRepo.delete(token);
    }

}

