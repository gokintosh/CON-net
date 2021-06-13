package com.gokul.authservice.service;


import com.gokul.authservice.exceptions.EmailAlreadyExistsException;
import com.gokul.authservice.exceptions.UsernameAlreadyExistsException;
import com.gokul.authservice.model.Role;
import com.gokul.authservice.model.User;
import com.gokul.authservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        log.info("retrieving all users");
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        log.info("retrieving user {}", username);
        return userRepository.findByUsername(username);
    }

    public List<User> findByUsernameIn(List<String> usernames) {
        return userRepository.findByUsernameIn(usernames);
    }

    public User registerUser(User user){
        log.info("registering user {}",user.getUsername());


        if(userRepository.existsByUsername(user.getUsername())){
            log.warn("username already exist {}",user.getUsername());

            throw new UsernameAlreadyExistsException(
                    String.format("username %s already exists",user.getUsername())
            );
        }

        if(userRepository.existsByEmail(user.getEmail())){
            log.warn("email {} already exists !",user.getEmail());

            throw new EmailAlreadyExistsException(
                    String.format("email %s already exists",user.getEmail())
            );
        }

        user.setActive(true);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>(){{
            add(Role.USER);
        }});


        User savedUser=userRepository.save(user);

        return savedUser;


    }


}
