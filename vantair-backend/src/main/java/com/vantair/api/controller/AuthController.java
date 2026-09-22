package com.vantair.api.controller;

import com.vantair.api.dto.Dtos.SignInRequest;
import com.vantair.api.dto.Dtos.SignUpRequest;
import com.vantair.api.model.User;
import com.vantair.api.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Customer sign up and sign in")
public class AuthController {

    private final UserService users;

    public AuthController(UserService users) {
        this.users = users;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@Valid @RequestBody SignUpRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(users.signUp(req));
    }

    @PostMapping("/signin")
    public User signIn(@Valid @RequestBody SignInRequest req) {
        return users.signIn(req);
    }
}
