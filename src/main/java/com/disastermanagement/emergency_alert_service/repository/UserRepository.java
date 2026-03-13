package com.disastermanagement.emergency_alert_service.repository;

import com.disastermanagement.emergency_alert_service.entity.Role;
import com.disastermanagement.emergency_alert_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByRoleAndStatus(Role responder, String available);
}
