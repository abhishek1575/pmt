package com.project.pmt.service;



import com.project.pmt.dto.request.LoginRequest;
import com.project.pmt.dto.request.RegisterRequest;
import com.project.pmt.dto.response.AuthResponse;
import com.project.pmt.entity.User;
import com.project.pmt.enums.Role;
import com.project.pmt.exceptions.BadRequestException;
import com.project.pmt.exceptions.DuplicateResourceException;
import com.project.pmt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Authentication Service
 * Handles user registration, login, token refresh, and UserDetailsService implementation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Load user by username for Spring Security
     * This method is called by Spring Security during authentication
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (!user.getIsActive()) {
            log.warn("Attempt to login with inactive user: {}", username);
            throw new UsernameNotFoundException("User account is inactive");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                user.getIsActive(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    /**
     * Register a new user
     *
     * @param request RegisterRequest containing user details
     * @return AuthResponse with JWT tokens and user info
     * @throws DuplicateResourceException if username or email already exists
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed: Username already exists - {}", request.getUsername());
            throw new DuplicateResourceException("User", "username");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new DuplicateResourceException("User", "email");
        }

        // Create new user entity
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDepartment(request.getDepartment());
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);
        user.setIsActive(true);

        user = userRepository.save(user);
        log.info("User registered successfully: {} with ID: {}", user.getUsername(), user.getId());

        // Generate JWT tokens
        UserDetails userDetails = loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    /**
     * Login user
     *
     * @param request LoginRequest containing username and password
     * @return AuthResponse with JWT tokens and user info
     * @throws UsernameNotFoundException if user not found
     * @throws BadRequestException if account is disabled
     */
    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getUsername());

        // Authenticate user credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Load user details from database
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found after successful authentication: {}", request.getUsername());
                    return new UsernameNotFoundException("User not found");
                });

        // Check if user is active
        if (!user.getIsActive()) {
            log.warn("Login attempt for inactive user: {}", request.getUsername());
            throw new BadRequestException("User account is disabled. Please contact administrator.");
        }

        // Generate JWT tokens
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        log.info("User logged in successfully: {} (ID: {})", user.getUsername(), user.getId());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    /**
     * Refresh access token using refresh token
     *
     * @param refreshToken Refresh token
     * @return AuthResponse with new access token
     * @throws BadRequestException if refresh token is invalid
     */
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Token refresh requested");

        // Validate refresh token
        if (!jwtService.validateToken(refreshToken)) {
            log.warn("Invalid refresh token provided");
            throw new BadRequestException("Invalid or expired refresh token");
        }

        // Extract username from token
        String username = jwtService.extractUsername(refreshToken);

        // Load user details
        UserDetails userDetails = loadUserByUsername(username);

        // Load user entity for additional info
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found during token refresh: {}", username);
                    return new UsernameNotFoundException("User not found");
                });

        // Check if user is still active
        if (!user.getIsActive()) {
            log.warn("Token refresh attempted for inactive user: {}", username);
            throw new BadRequestException("User account is disabled");
        }

        // Generate new access token
        String newToken = jwtService.generateToken(userDetails);

        log.info("Token refreshed successfully for user: {}", username);

        return AuthResponse.builder()
                .token(newToken)
                .refreshToken(refreshToken) // Return same refresh token
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    /**
     * Check if username exists
     *
     * @param username Username to check
     * @return true if exists, false otherwise
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     *
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}