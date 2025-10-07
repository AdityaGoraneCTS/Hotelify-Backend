package com.cts.user_service.config;

import com.cts.user_service.entity.Roles;
import com.cts.user_service.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolesRepository rolesRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize default roles if they don't exist
        createRoleIfNotExists("USER");
        createRoleIfNotExists("ADMIN");
    }
    
    private void createRoleIfNotExists(String roleName) {
        if (rolesRepository.findByName(roleName).isEmpty()) {
            Roles role = new Roles();
            role.setName(roleName);
            rolesRepository.save(role);
        }
    }
}