package uns.ftn.kms.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uns.ftn.kms.dtos.auth.requests.LoginRequest;
import uns.ftn.kms.dtos.auth.requests.RegisterRequest;
import uns.ftn.kms.dtos.auth.responses.LoginResponse;
import uns.ftn.kms.dtos.auth.responses.UserResponse;
import uns.ftn.kms.services.auth.IAuthService;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("api/auth")
public class AuthController {
    private final IAuthService service;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        return new ResponseEntity<>(service.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        service.logout();
        return ResponseEntity.noContent().build();
    }
}