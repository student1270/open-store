package ru.gb.service.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.gb.model.Roles;
import ru.gb.model.User;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Roles role = user.getRole();
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        // siz User modelida password yo‘q dedingiz, agar password saqlanmasa, bu xatolik beradi
        // lekin agar qo‘shilgan bo‘lsa (tavsiya qilinadi), bu metod ishlaydi
        throw new UnsupportedOperationException("Parol mavjud emas. User modelida 'password' maydonini qo‘shing.");
    }

    @Override
    public String getUsername() {
        return user.getEmailAddress(); // email orqali login bo‘lsa
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
