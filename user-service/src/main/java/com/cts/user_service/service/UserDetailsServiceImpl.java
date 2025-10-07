package com.cts.user_service.service;

import com.cts.user_service.entity.User;
import com.cts.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        System.out.println("Loading user: " + user.getUsername() + ", roles: " + user.getRoles());
        
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> {
                    String authority = "ROLE_" + role.getName();
                    System.out.println("Adding authority: " + authority);
                    return new SimpleGrantedAuthority(authority);
                })
                .collect(Collectors.toList());

        System.out.println("Final authorities: " + authorities);
        
        // Use the login identifier (username parameter) instead of user.getUsername()
        // This ensures JWT token subject matches UserDetails username
        return org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}