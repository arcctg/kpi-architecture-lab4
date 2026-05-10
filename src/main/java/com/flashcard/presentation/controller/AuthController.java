package com.flashcard.presentation.controller;

import com.flashcard.application.dto.AuthResult;
import com.flashcard.application.usecase.auth.LoginUseCase;
import com.flashcard.application.usecase.auth.RegisterUseCase;
import com.flashcard.presentation.dto.request.LoginRequest;
import com.flashcard.presentation.dto.request.RegisterRequest;
import com.flashcard.presentation.dto.response.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;

    public AuthController(RegisterUseCase registerUseCase, LoginUseCase loginUseCase) {
        this.registerUseCase = registerUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResult result = registerUseCase.execute(
                request.email(), request.displayName(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(result.token()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = loginUseCase.execute(request.email(), request.password());
        return ResponseEntity.ok(new AuthResponse(result.token()));
    }
}
