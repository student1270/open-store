package ru.gb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gb.model.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review , Long> {
    @Query(value = "SELECT * FROM review WHERE product_id = :id ORDER BY writed_time DESC", nativeQuery = true)
    List<Review> findByProductIdOrderByLocalTimeDesc(@Param("id") Long id);

    List<Review> findTop2ByProductIdOrderByLocalDateTimeDesc(Long id);

}
