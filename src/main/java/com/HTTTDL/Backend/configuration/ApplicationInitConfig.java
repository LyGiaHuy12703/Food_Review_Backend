package com.HTTTDL.Backend.configuration;

import com.HTTTDL.Backend.enums.Role;
import com.HTTTDL.Backend.model.User;
import com.HTTTDL.Backend.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;
    //dduowjc khoi chay khiu du an duoc start
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findAll().isEmpty()) {
                var roles = new HashSet<String>();
                roles.add(Role.ADMIN.toString());

                User admin = new User();
                admin.setRoles(roles);
                admin.setName("Admin");
                admin.setUsername("admin@email.com");
                admin.setPassword(passwordEncoder.encode("admin"));
                userRepository.save(admin);

                log.warn("admin has been created with default password: admin, please change it");
            }
        };
    };
}
