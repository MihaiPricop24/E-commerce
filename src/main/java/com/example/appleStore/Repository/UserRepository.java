package com.example.appleStore.Repository;

import com.example.appleStore.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
    Boolean existsByEmail(String email);
}
