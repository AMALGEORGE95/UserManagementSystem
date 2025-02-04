package com.demo.user_management_system.repository;

import com.demo.user_management_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);

    List<User> findAllByIsDeletedFalse();
    @Query(value = "SELECT * FROM users WHERE email = ?1 AND is_deleted = false", nativeQuery = true)
    Optional<User> findByEmailAndIsDeletedFalse(String email);

}
