package ru.gb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.gb.model.Admin;
import ru.gb.repository.AdminRepository;

@Service
@RequiredArgsConstructor
public class AdminDetailsService implements UserDetailsService {

    private static final String ROLE_PREFIX = "ROLE_";

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Foydalanuvchi '" + username + "' topilmadi"));
        return User
                .withUsername(admin.getUsername())
                .password(admin.getPassword())
                .authorities(ROLE_PREFIX + admin.getRole().getValue())
                .build();
    }
}