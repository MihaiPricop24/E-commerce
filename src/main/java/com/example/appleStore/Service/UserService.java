package com.example.appleStore.Service;

import com.example.appleStore.Model.User;
import com.example.appleStore.Repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return  userRepository.save(user);
    }

    public User loginUser(String email, String password) throws Exception {

        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new Exception("User not found");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Invalid password");
        }
        return user;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }


    public User updateUser(long id, User user) {
        User exists = userRepository.findById(id).orElse(null);
        if (exists == null) {
           return null;
        }
        return userRepository.save(user);
    }

}
