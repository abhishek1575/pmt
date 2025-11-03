package com.project.pmt.service;

import com.project.pmt.dto.response.UserResponse;
import com.project.pmt.entity.User;
import com.project.pmt.enums.Role;
import com.project.pmt.exceptions.ResourceNotFoundException;
import com.project.pmt.exceptions.UnauthorizedException;
import com.project.pmt.mapper.UserMapper;
import com.project.pmt.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse getCurrentUser(){
        User user = getCurrentUserEntity();
        return userMapper.toResponse(user);
    }

    public User getCurrentUserEntity(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()){
            log.error("No Authenticated User found in security context");
            throw new UnauthorizedException("No Authenticated user found");
        }

        Object principal = authentication.getPrincipal();

        String username;
        if (principal instanceof UserDetails){
            username = ((UserDetails)principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        }else {
            log.error("Unknown principal type:{}", principal.getClass().getName());
            throw new UnauthorizedException("Invalid Authentication principal");
        }
        return userRepository.findByUsername(username)
                .orElseThrow(()->{
                    log.error("User not found with username: {}", username);
                    return new ResourceNotFoundException("User", "username", username);
                });
    }

    public UserResponse getUserByUsername (String username){
        log.debug("Fetching user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->
                    new ResourceNotFoundException("Username", "username", username)
                );
        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAllUsers(){
        log.debug("Get all users");
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getActiveUsers(){
        log.debug("Fetch all active users");
        return userRepository.findByActiveTrue().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> searchUsers(String search){
        log.debug("Search user with query: {}", search);
        return userRepository.searchUsers(search).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateUser ( Long id, UserResponse updateRequest){
        log.info("updating user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User", "id", id));
        if (updateRequest.getFullName() != null && !updateRequest.getFullName().isBlank()){
            user.setFullName(updateRequest.getFullName());
            log.debug("Updated full name to: {}", id);
        }
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().isBlank()){
            userRepository.findByEmail(updateRequest.getEmail())
                    .ifPresent(existingUser -> {
                        if(existingUser.getId().equals(id)){
                            throw new ResourceNotFoundException("Email is already in use");
                        }
                    });
            user.setEmail(updateRequest.getEmail());
            log.debug("Updated email to: {}", id);
        }

        if (updateRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(updateRequest.getPhoneNumber());
            log.debug("Updated phone number for user {}", id);
        }

        if (updateRequest.getDepartment() != null) {
            user.setDepartment(updateRequest.getDepartment());
            log.debug("Updated department for user {}", id);
        }

        if (updateRequest.getAvatarUrl() != null) {
            user.setAvatarUrl(updateRequest.getAvatarUrl());
            log.debug("Updated avatar URL for user {}", id);
        }

        user = userRepository.save(user);
        log.info("User updated successfully: {}", user.getId());

        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateUserRole(Long id, Role role) {
        log.info("Updating user role: {} -> {}", id, role);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setRole(role);
        user = userRepository.save(user);
        log.info("User role updated successfully: {} -> {}", user.getId(), role);

        return userMapper.toResponse(user);
    }

    @Transactional
    public void deactivateUser(Long id) {
        log.info("Deactivating user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setIsActive(false);
        userRepository.save(user);
        log.info("User deactivated successfully: {}", user.getId());
    }

    @Transactional
    public void activateUser(Long id) {
        log.info("Activating user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setIsActive(true);
        userRepository.save(user);
        log.info("User activated successfully: {}", user.getId());
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userRepository.delete(user);
        log.info("User deleted successfully: {}", id);
    }

    public User findUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }


    public User findUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<UserResponse> getUsersByRole(Role role) {
        log.debug("Fetching users with role: {}", role);
        return userRepository.findByRole(role).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public boolean currentUserHasRole(Role role) {
        try {
            User currentUser = getCurrentUserEntity();
            return currentUser.getRole().equals(role);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean currentUserIsAdmin() {
        try {
            User currentUser = getCurrentUserEntity();
            return currentUser.getRole().equals(Role.ADMIN) ||
                    currentUser.getRole().equals(Role.SUPER_ADMIN);
        } catch (Exception e) {
            return false;
        }
    }

}
