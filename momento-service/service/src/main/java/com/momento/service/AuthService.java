package com.momento.service;

import com.momento.data.model.UserAccount;
import com.momento.data.model.enums.UserStatus;
import com.momento.data.repository.UserRepository;
import com.momento.exception.BusinessException;
import com.momento.exception.DuplicateResourceException;
import com.momento.exception.ResourceNotFoundException;
import com.momento.service.dto.in.LoginRequest;
import com.momento.service.dto.in.RegisterRequest;
import com.momento.service.dto.out.LoginResponse;
import com.momento.service.dto.out.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        LOGGER.info("Registering new user with email: {}", request.getEmail());
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            LOGGER.warn("Registration failed: Email {} already exists", request.getEmail());
            throw new DuplicateResourceException("Email already exists");
        }

        UserAccount user = new UserAccount();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus(UserStatus.ACTIVE);

        user = userRepository.save(user);

        return mapToResponse(user);
    }

    public LoginResponse login(LoginRequest request) {
        LOGGER.info("Login attempt for email: {}", request.getEmail());
        UserAccount user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    LOGGER.warn("Login failed: User not found for email {}", request.getEmail());
                    return new ResourceNotFoundException("User not found");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            LOGGER.warn("Login failed: Invalid password for email {}", request.getEmail());
            throw new BusinessException("Invalid password");
        }

        LOGGER.info("Login successful for email: {}", request.getEmail());
        String token = jwtService.generateToken(user);
        return new LoginResponse(token, mapToResponse(user));
    }

    public UserResponse getCurrentUser(String email) {
        LOGGER.debug("Fetching current user for email: {}", email);
        UserAccount user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }

    public UserResponse mapToResponse(UserAccount user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        return response;
    }
}
