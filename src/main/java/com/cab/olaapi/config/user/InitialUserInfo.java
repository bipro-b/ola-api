package com.cab.olaapi.config.user;

import com.cab.olaapi.entity.UserInfoEntity;
import com.cab.olaapi.repo.UserInfoRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Bipro
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class InitialUserInfo implements CommandLineRunner {
    private final UserInfoRepo userInfoRepo;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) throws Exception {
        if (userInfoRepo.findByEmailId("manager@manager.com") == null) {
            UserInfoEntity manager = new UserInfoEntity();
            manager.setUserName("Manager");
            manager.setPassword(passwordEncoder.encode("password"));
            manager.setRoles("ROLE_MANAGER");
            manager.setEmailId("manager@manager.com");
            userInfoRepo.saveAll(List.of(manager));

        }
        if (userInfoRepo.findByEmailId("admin@admin.com") == null) {
            UserInfoEntity admin = new UserInfoEntity();
            admin.setUserName("Admin");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRoles("ROLE_ADMIN");
            admin.setEmailId("admin@admin.com");
            userInfoRepo.saveAll(List.of(admin));

        }
        if (userInfoRepo.findByEmailId("user@user.com") == null) {
            UserInfoEntity user = new UserInfoEntity();
            user.setUserName("User");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRoles("ROLE_USER");
            user.setEmailId("user@user.com");
            userInfoRepo.saveAll(List.of(user));

        }
    }

}