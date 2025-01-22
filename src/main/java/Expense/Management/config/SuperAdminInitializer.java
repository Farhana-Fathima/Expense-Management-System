package Expense.Management.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import Expense.Management.model.Role;
import Expense.Management.model.User;
import Expense.Management.repository.UserRepository;


@Component
@Configuration
public class SuperAdminInitializer {

    @Value("${super.admin.email}")
    private String superAdminEmail;

    @Value("${super.admin.password}")
    private String superAdminPassword;

    @Bean
    public CommandLineRunner initializeSuperAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail(superAdminEmail)) {
                User superAdmin = User.builder()
                    .username("superadmin")
                    .email(superAdminEmail)
                    .password(passwordEncoder.encode(superAdminPassword))
                    .role(Role.SUPER_ADMIN)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

                userRepository.save(superAdmin);
                System.out.println("Super Admin created with email: " + superAdminEmail);
            } else {
                System.out.println("Super Admin already exists with email: " + superAdminEmail);
            }
        };
    }
}
