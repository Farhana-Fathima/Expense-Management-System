package Expense.Management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Expense.Management.DTO.LoginRequest;
import Expense.Management.DTO.LoginResponse;
import Expense.Management.DTO.RefreshTokenRequest;
import Expense.Management.DTO.RegisterRequest;
import Expense.Management.service.AuthenticationService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    // Registration endpoint
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        // Call the registration service which handles email validation, registration, and sending the verification email
        String message = authenticationService.register(request);
        return ResponseEntity.ok(message);  // Returning success message after registration
    }

    // Email verification endpoint
    @GetMapping("/verify-email")
    public String verifyEmail(@Valid @RequestParam String token) {
        // Call the service to verify the email using the verification token
        authenticationService.verifyEmail(token);
        return "Email verified successfully! You can now log in."; // Return a success message on successful email verification
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // Call the login service to authenticate the user and generate the JWT token
        LoginResponse loginResponse = authenticationService.login(request);
        return ResponseEntity.ok(loginResponse);  // Return the generated JWT token upon successful login
    }

     @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        authenticationService.logout(token);
        return ResponseEntity.ok("Logout successful.");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
    return ResponseEntity.ok(authenticationService.refreshToken(request));
}

    
}











// package Expense.Management.controller;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestHeader;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import Expense.Management.DTO.LoginRequest;
// import Expense.Management.DTO.LoginResponse;
// import Expense.Management.DTO.RefreshTokenRequest;
// import Expense.Management.DTO.RegisterRequest;
// import Expense.Management.service.AuthenticationService;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/api/auth")
// @RequiredArgsConstructor
// public class AuthenticationController {

//     private final AuthenticationService authenticationService;

//     // Registration endpoint
//     @PostMapping("/register")
//     public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
//         // Call the registration service which handles email validation, registration, and sending the verification email
//         String message = authenticationService.register(request);
//         return ResponseEntity.ok(message);  // Returning success message after registration
//     }

//     // Email verification endpoint
//     @GetMapping("/verify-email")
//     public String verifyEmail(@Valid @RequestParam String token) {
//         // Call the service to verify the email using the verification token
//         authenticationService.verifyEmail(token);
//         return "Email verified successfully! You can now log in."; // Return a success message on successful email verification
//     }

//     // Login endpoint
//     @PostMapping("/login")
//     public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
//         // Call the login service to authenticate the user and generate the JWT token
//         LoginResponse loginResponse = authenticationService.login(request);
//         return ResponseEntity.ok(loginResponse);  // Return the generated JWT token upon successful login
//     }

//      @PostMapping("/logout")
//     public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
//         authenticationService.logout(token);
//         return ResponseEntity.ok("Logout successful.");
//     }

//     @PostMapping("/refresh-token")
//     public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
//     return ResponseEntity.ok(authenticationService.refreshToken(request));
// }

    
// }