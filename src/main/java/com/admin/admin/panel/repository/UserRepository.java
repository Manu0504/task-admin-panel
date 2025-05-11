package com.admin.admin.panel.repository;

import com.admin.admin.panel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    List<User> findByRole(String role);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.password = :password")
    Optional<User> login(@Param("username") String username, @Param("password") String password);

    // Add more custom queries as needed for your admin panel
}