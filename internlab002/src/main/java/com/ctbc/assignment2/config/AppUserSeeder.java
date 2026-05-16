package com.ctbc.assignment2.config;

import com.ctbc.assignment2.service.AppUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@Order(-1)
public class AppUserSeeder implements CommandLineRunner {

    private final AppUserService appUserService;

    public AppUserSeeder(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @Override
    public void run(String... args) {
        System.out.println("========== 建立預設使用者 ==========");

        if (!appUserService.existsByUsername("admin")) {
            appUserService.createUser("admin", "admin123", "ADMIN");
        }
        if (!appUserService.existsByUsername("user")) {
            appUserService.createUser("user", "user123", "USER");
        }

        String[] instructors = {"instructor1", "instructor2", "instructor3"};
        for (String name : instructors) {
            if (!appUserService.existsByUsername(name)) {
                appUserService.registerInstructor(name, name + "123");
            }
        }

        System.out.println("使用者初始化完成");
    }
}
