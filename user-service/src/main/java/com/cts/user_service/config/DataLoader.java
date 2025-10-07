package com.cts.user_service.config;

import com.cts.user_service.entity.Roles;
import com.cts.user_service.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * This component runs on application startup and seeds the database with
 * essential data, such as user roles, if they don't already exist.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final RolesRepository roleRepository;

    @Autowired
    public DataLoader(RolesRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("DataLoader running...");
        createRoleIfNotFound("USER");
        createRoleIfNotFound("ADMIN");
        System.out.println("Roles have been checked/seeded.");
    }

    /**
     * Helper method to check if a role exists, and create it if it doesn't.
     * This makes the seeding process idempotent (safe to run multiple times).
     */
    private void createRoleIfNotFound(String roleName) {
        Optional<Roles> role = roleRepository.findByName(roleName);
        if (role.isEmpty()) {
            roleRepository.save(new Roles(null, roleName));
            System.out.println("Created role: " + roleName);
        }
    }
}
