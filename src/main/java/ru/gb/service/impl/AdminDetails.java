package ru.gb.service.impl;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.gb.model.Admin;

import java.util.Collection;


import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@Getter
public class AdminDetails implements UserDetails {

    private final Admin admin;

    public AdminDetails(Admin admin) {
        this.admin = admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return createAuthorityList("ROLE_" + admin.getRole().getValue());
    }

    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    @Override
    public String getUsername() {
        return admin.getUsername();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
