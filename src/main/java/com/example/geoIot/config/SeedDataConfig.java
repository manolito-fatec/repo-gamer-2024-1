package com.example.geoIot.config;

import com.example.geoIot.entity.dto.UserDto;
import com.example.geoIot.repository.UserRepository;
import com.example.geoIot.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeedDataConfig implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            UserDto admin = UserDto
                    .builder()
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("admin"))
                    .role("ADMIN")
                    .build();

            userService.createUser(admin);
            log.debug("Created admin user - {}", admin);
        }
    }
}
