package ru.gb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.model.Admin;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin , Integer> {
    Optional<Admin> findByUsername(String name);
}
