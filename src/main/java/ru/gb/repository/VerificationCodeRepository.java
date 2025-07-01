package ru.gb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.gb.model.VerificationCode;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByUserIdAndCodeAndIsUsedFalseAndExpiryDateAfter(Long userId, String code, LocalDateTime now);

    @Query("SELECT COUNT(v) FROM VerificationCode v WHERE v.ipAddress = :ipAddress AND v.createdAt > :since")
    long countByIpAddressAndCreatedAtAfter(String ipAddress, LocalDateTime since);

    Optional<VerificationCode> findFirstByIpAddressAndBlockedUntilAfter(String ipAddress, LocalDateTime now);

    @Modifying
    @Query("DELETE FROM VerificationCode v WHERE v.expiryDate < :now OR v.isUsed = true")
    void deleteExpiredOrUsedCodes(LocalDateTime now);

    @Modifying
    @Query("DELETE FROM VerificationCode v WHERE v.user.id = :userId AND v.isUsed = false AND v.expiryDate > :now")
    void deleteByUserIdAndIsUsedFalseAndExpiryDateAfter(Long userId, LocalDateTime now);
}