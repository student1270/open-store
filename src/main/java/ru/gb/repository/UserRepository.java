package ru.gb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}