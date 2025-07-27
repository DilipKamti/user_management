package com.microservice.user.management.service.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.microservice.user.management.service.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
}
