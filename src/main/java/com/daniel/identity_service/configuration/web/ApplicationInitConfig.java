package com.daniel.identity_service.configuration.web;

import com.daniel.identity_service.entity.Role;
import com.daniel.identity_service.entity.User;
import com.daniel.identity_service.repository.RoleRepository;
import com.daniel.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
    @Transactional
    ApplicationRunner applicationRunner(UserRepository userRepository,
                                        RoleRepository roleRepository) {
        return args -> {
            // 1. Khởi tạo role ADMIN nếu chưa có
            Role adminRole = roleRepository.findById("ADMIN")
                    .orElseGet(() -> {
                        Role role = roleRepository.save(Role.builder()
                                .name("ADMIN")
                                .description("Administrator role")
                                .build());
                        log.info("Admin role created");
                        return role;
                    });

            // 2. Khởi tạo role USER nếu chưa có
            if (roleRepository.findById("USER").isEmpty()) {
                roleRepository.save(Role.builder()
                        .name("USER")
                        .description("Default user role")
                        .build());
                log.info("User role created");
            }

            // 3. Khởi tạo tài khoản admin mặc định
            if (userRepository.findByUsername("admin").isEmpty()) {
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);

                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.info("Default admin user created successfully (admin/admin)");
            }
        };
    }
}
