package Expense.Management.service;

import Expense.Management.DTO.LoginRequest;
import Expense.Management.DTO.LoginResponse;
import Expense.Management.DTO.RefreshTokenRequest;
import Expense.Management.DTO.RegisterRequest;
import Expense.Management.model.Role;
import Expense.Management.model.User;
import Expense.Management.repository.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Session;
import javax.mail.Transport;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final TokenBlacklistService tokenBlacklistService;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public String register(RegisterRequest request) {
        // Validate email format
        if (!Pattern.matches(EMAIL_REGEX, request.getEmail())) {
            throw new ValidationException("Invalid email format.");
        }

        // Validate email existence via SMTP
        if (!isEmailValid(request.getEmail())) {
            throw new ValidationException("The email address does not exist.");
        }

        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ValidationException("Username already exists.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already exists.");
        }

        // Validate password length
        if (request.getPassword().length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long.");
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .verificationToken(UUID.randomUUID().toString()) // Generate a unique token
                .isEmailVerified(false) // Initially not verified
                .active(true) // Default active status
                .build();

        User savedUser = userRepository.save(user);

        // Send a verification email with the verification link
        emailService.sendEmailVerification(savedUser.getEmail(), savedUser.getVerificationToken());

        // Return a user-friendly message
        return "Registration successful! A verification link has been sent to " + savedUser.getEmail() + ". Please verify your email to proceed.";
    }

    private boolean isEmailValid(String email) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); // Update with a suitable SMTP host
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "false");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties);
        try (Transport transport = session.getTransport("smtp")) {
            transport.connect("smtp.gmail.com", email, null); // Attempt connection
            return true; // If the connection succeeds, email exists
        } catch (Exception e) {
            // Log error for debugging, if needed
            System.out.println("Email validation failed: " + e.getMessage());
            return false;
        }
    }

    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ValidationException("Invalid verification token."));

        user.setEmailVerified(true);
        user.setVerificationToken(null); // Remove the verification token after successful verification
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
    
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ValidationException("User not found."));
    
        // Check if the user's email is verified for regular users
        if (user.getRole() == Role.USER && !user.isEmailVerified()) {
            throw new ValidationException("Email not verified. Please verify your email before logging in.");
        }
    
        // Generate both access and refresh tokens
        String accessToken = jwtService.generateToken((UserDetails) user);
        String refreshToken = jwtService.generateRefreshToken((UserDetails) user);
    
        return LoginResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)  // Include the refresh token in the response
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public void logout(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        long expirationTime = jwtService.extractExpiration(token).getTime() - System.currentTimeMillis();
        tokenBlacklistService.blacklistToken(token, expirationTime);
    }

    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String username = jwtService.extractUsername(request.getRefreshToken());
    
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ValidationException("User not found."));
    
        if (!jwtService.isTokenValid(request.getRefreshToken(), user)) {
            throw new ValidationException("Invalid refresh token.");
        }
    
        String newAccessToken = jwtService.generateToken(user); // Generates a new access token
        String newRefreshToken = jwtService.generateRefreshToken(user); // Generate a new refresh token
    
        return LoginResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken) // Ensure this field is populated
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
    

    
}









// package Expense.Management.service;

// import Expense.Management.DTO.LoginRequest;
// import Expense.Management.DTO.LoginResponse;
// import Expense.Management.DTO.RefreshTokenRequest;
// import Expense.Management.DTO.RegisterRequest;
// import Expense.Management.model.Role;
// import Expense.Management.model.User;
// import Expense.Management.repository.UserRepository;
// import jakarta.validation.ValidationException;
// import lombok.RequiredArgsConstructor;

// import org.springframework.stereotype.Service;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.crypto.password.PasswordEncoder;

// import java.util.regex.Pattern;
// import java.time.LocalDateTime;
// import java.util.Properties;
// import java.util.UUID;

// import javax.mail.Session;
// import javax.mail.Transport;

// @Service
// @RequiredArgsConstructor
// public class AuthenticationService {
//     private final UserRepository userRepository;
//     private final PasswordEncoder passwordEncoder;
//     private final JwtService jwtService;
//     private final AuthenticationManager authenticationManager;
//     private final EmailService emailService;
//     private final TokenBlacklistService tokenBlacklistService;

//     private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
//     public String register(RegisterRequest request) {
//         // Validate email format
//         if (!Pattern.matches(EMAIL_REGEX, request.getEmail())) {
//             throw new ValidationException("Invalid email format.");
//         }

//         // Validate email existence via SMTP
//         if (!isEmailValid(request.getEmail())) {
//             throw new ValidationException("The email address does not exist.");
//         }

//         // Check if username or email already exists
//         if (userRepository.existsByUsername(request.getUsername())) {
//             throw new ValidationException("Username already exists.");
//         }
//         if (userRepository.existsByEmail(request.getEmail())) {
//             throw new ValidationException("Email already exists.");
//         }

//         // Validate password length
//         if (request.getPassword().length() < 8) {
//             throw new ValidationException("Password must be at least 8 characters long.");
//         }

//         // Create new user
//         User user = User.builder()
//                 .username(request.getUsername())
//                 .email(request.getEmail())
//                 .password(passwordEncoder.encode(request.getPassword()))
//                 .role(Role.USER)
//                 .createdAt(LocalDateTime.now())
//                 .updatedAt(LocalDateTime.now())
//                 .verificationToken(UUID.randomUUID().toString()) // Generate a unique token
//                 .isEmailVerified(false) // Initially not verified
//                 .active(true) // Default active status
//                 .build();

//         User savedUser = userRepository.save(user);

//         // Send a verification email with the verification link
//         emailService.sendEmailVerification(savedUser.getEmail(), savedUser.getVerificationToken());

//         // Return a user-friendly message
//         return "Registration successful! A verification link has been sent to " + savedUser.getEmail() + ". Please verify your email to proceed.";
//     }

//     private boolean isEmailValid(String email) {
//         Properties properties = new Properties();
//         properties.put("mail.smtp.host", "smtp.gmail.com"); // Update with a suitable SMTP host
//         properties.put("mail.smtp.port", "587");
//         properties.put("mail.smtp.auth", "false");
//         properties.put("mail.smtp.starttls.enable", "true");

//         Session session = Session.getInstance(properties);
//         try (Transport transport = session.getTransport("smtp")) {
//             transport.connect("smtp.gmail.com", email, null); // Attempt connection
//             return true; // If the connection succeeds, email exists
//         } catch (Exception e) {
//             // Log error for debugging, if needed
//             System.out.println("Email validation failed: " + e.getMessage());
//             return false;
//         }
//     }

//     public void verifyEmail(String token) {
//         User user = userRepository.findByVerificationToken(token)
//                 .orElseThrow(() -> new ValidationException("Invalid verification token."));

//         user.setEmailVerified(true);
//         user.setVerificationToken(null); // Remove the verification token after successful verification
//         userRepository.save(user);
//     }

//     public LoginResponse login(LoginRequest request) {
//         authenticationManager.authenticate(
//                 new UsernamePasswordAuthenticationToken(
//                         request.getUsername(),
//                         request.getPassword()
//                 )
//         );
    
//         User user = userRepository.findByUsername(request.getUsername())
//                 .orElseThrow(() -> new ValidationException("User not found."));
    
//         // Check if the user's email is verified for regular users
//         if (user.getRole() == Role.USER && !user.isEmailVerified()) {
//             throw new ValidationException("Email not verified. Please verify your email before logging in.");
//         }
    
//         // Generate both access and refresh tokens
//         String accessToken = jwtService.generateToken((UserDetails) user);
//         String refreshToken = jwtService.generateRefreshToken((UserDetails) user);
    
//         return LoginResponse.builder()
//                 .token(accessToken)
//                 .refreshToken(refreshToken)  // Include the refresh token in the response
//                 .username(user.getUsername())
//                 .role(user.getRole())
//                 .build();
//     }

//     public void logout(String token) {
//         if (token.startsWith("Bearer ")) {
//             token = token.substring(7);
//         }
        
//         long expirationTime = jwtService.extractExpiration(token).getTime() - System.currentTimeMillis();
//         tokenBlacklistService.blacklistToken(token, expirationTime);
//     }

//     public LoginResponse refreshToken(RefreshTokenRequest request) {
//         String username = jwtService.extractUsername(request.getRefreshToken());
    
//         User user = userRepository.findByUsername(username)
//                 .orElseThrow(() -> new ValidationException("User not found."));
    
//         if (!jwtService.isTokenValid(request.getRefreshToken(), user)) {
//             throw new ValidationException("Invalid refresh token.");
//         }
    
//         String newAccessToken = jwtService.generateToken(user); // Generates a new access token
//         String newRefreshToken = jwtService.generateRefreshToken(user); // Generate a new refresh token
    
//         return LoginResponse.builder()
//                 .token(newAccessToken)
//                 .refreshToken(newRefreshToken) // Ensure this field is populated
//                 .username(user.getUsername())
//                 .role(user.getRole())
//                 .build();
//     }
    

    
// }
