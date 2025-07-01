package ru.gb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.gb.model.User;
import ru.gb.repository.UserRepository;



@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public boolean isEmailExists(String emailAddress) {
        if (emailAddress == null || emailAddress.trim().isEmpty()) {
            return false;
        }
        return userRepository.findByEmailAddress(emailAddress).isPresent();
    }

    public boolean isPhoneExists(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    public boolean saveUser(User user) {
        if (user == null) {
            return false;
        }

        String cleanedPhone = user.getPhoneNumber().replaceAll("\\D", "");

        boolean checkName = user.getName() != null && !user.getName().trim().isEmpty();
        boolean checkSurname = user.getSurname() != null && !user.getSurname().trim().isEmpty();
        boolean checkEmailAddress = user.getEmailAddress() != null &&
                user.getEmailAddress().endsWith("@gmail.com") &&
                !isEmailExists(user.getEmailAddress());
        boolean checkPhoneNumber = cleanedPhone.matches("\\d{9}") && !isPhoneExists(cleanedPhone);

        if (checkName && checkSurname && checkEmailAddress && checkPhoneNumber) {
            user.setPhoneNumber(cleanedPhone);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new UsernameNotFoundException("Foydalanuvchi topilmadi: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmailAddress())  // Email asosida autentifikatsiya
                .password("")  // Parol kerak bo'lmasa bo'sh qoldiring
                .roles("USER")
                .build();
    }
}