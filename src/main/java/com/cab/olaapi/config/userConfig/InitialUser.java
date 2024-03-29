package com.cab.olaapi.config.userConfig;

import com.cab.olaapi.dto.UserRegistrationDto;
import com.cab.olaapi.entity.User;
import com.cab.olaapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class InitialUser implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmailId("manager@manager.com") == null) {
            User manager = new User();
            manager.setUserName("Manager");
            manager.setPassword(passwordEncoder.encode("123456"));
            manager.setRoles("ROLE_MANAGER");
            manager.setEmailId("manager@manager.com");
            userRepository.save(manager);
        }
        if (userRepository.findByEmailId("admin@admin.com") == null) {
            User admin = new User();
            admin.setUserName("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRoles("ROLE_ADMIN");
            admin.setEmailId("admin@admin.com");
            userRepository.save(admin);
        }

        if (userRepository.findByEmailId("user@user.com") == null) {
            User user = new User();
            user.setUserName("User");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRoles("ROLE_USER");
            user.setEmailId("user@user.com");
            userRepository.save(user);
        }

    }
}
