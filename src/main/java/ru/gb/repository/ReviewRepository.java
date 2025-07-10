package ru.gb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gb.model.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId ORDER BY r.localDateTime DESC")
    List<Review> findReviewsByProductIdDesc(@Param("productId") Long productId);

    @Query("SELECT r FROM Review r WHERE r.product.id = :productId ORDER BY r.localDateTime DESC LIMIT 2")
    List<Review> findTop2ByProductIdOrderByLocalDateTimeDesc(@Param("productId") Long productId);
}
