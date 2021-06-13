package com.gokul.authservice.controller;


import com.gokul.authservice.dto.ApiResponse;
import com.gokul.authservice.dto.SignUpRequest;
import com.gokul.authservice.exceptions.BadRequestException;
import com.gokul.authservice.exceptions.EmailAlreadyExistsException;
import com.gokul.authservice.exceptions.UsernameAlreadyExistsException;
import com.gokul.authservice.model.Profile;
import com.gokul.authservice.model.User;
import com.gokul.authservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;



    @PostMapping(value = "/users",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@Valid @RequestBody SignUpRequest payload){
        log.info("creating user {}",payload.getUsername());

        User user=User.builder()
                .username(payload.getUsername())
                .email(payload.getEmail())
                .password(payload.getPassword())
                .userProfile(Profile
                        .builder()
                        .displayName(payload.getName())
                        .build())
                .build();

        try{
            userService.registerUser(user);
        }catch (UsernameAlreadyExistsException| EmailAlreadyExistsException e){
            throw new BadRequestException(e.getMessage());
        }

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(user.getUsername()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true,"user registered successfully"));
    }
}
