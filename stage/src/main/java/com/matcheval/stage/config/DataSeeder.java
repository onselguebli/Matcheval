package com.matcheval.stage.config;

import com.matcheval.stage.model.Civility;
import com.matcheval.stage.model.Roles;
import com.matcheval.stage.model.Users;
import com.matcheval.stage.repo.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;

@Configuration
public class DataSeeder {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Bean
    CommandLineRunner seedUsers(UserRepo userRepo) {
        return args -> {

            // ✅ 1) Admin
            if (!userRepo.existsByEmail("admin1@matcheval.com")) {
                Users admin = new Users();
                admin.setFirstname("Admin1");
                admin.setLastname("Matcheval");
                admin.setEmail("admin1@matcheval.com");
                admin.setPassword(encoder.encode("Admin123!"));
                admin.setRole(Roles.ADMIN);
                admin.setCivility(Civility.SINGLE);
                admin.setCreatedAt(new Date());
                admin.setEnabled(true);
                admin.setLocked(false);
                userRepo.save(admin);
            }

            // ✅ 2) Manager
            Users manager = userRepo.findByEmail("manager1@matcheval.com");
            if (manager == null) {
                manager = new Users();
                manager.setFirstname("Manager1");
                manager.setLastname("Matcheval");
                manager.setEmail("manager1@matcheval.com");
                manager.setPassword(encoder.encode("Manager123!"));
                manager.setRole(Roles.MANAGER);
                manager.setCivility(Civility.SINGLE);
                manager.setCreatedAt(new Date());
                manager.setEnabled(true);
                manager.setLocked(false);
                manager = userRepo.save(manager);
            }

            // ✅ 3) Recruteur (lié au manager)
            if (!userRepo.existsByEmail("recruteur1@matcheval.com")) {
                Users recruiter = new Users();
                recruiter.setFirstname("Recruteur1");
                recruiter.setLastname("Matcheval");
                recruiter.setEmail("recruteur1@matcheval.com");
                recruiter.setPassword(encoder.encode("Recruiter123!"));
                recruiter.setRole(Roles.RECRUITER);
                recruiter.setCivility(Civility.SINGLE);
                recruiter.setCreatedAt(new Date());
                recruiter.setEnabled(true);
                recruiter.setLocked(false);

                recruiter.setManager(manager); // ✅ liaison obligatoire
                userRepo.save(recruiter);
            }
        };
    }
}
