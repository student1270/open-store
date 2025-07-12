package ru.gb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.gb.model.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.categoryName = :name AND (c.parent IS NULL OR c.parent.id = :parentId)")
    Optional<Category> findByCategoryNameAndParentIdCustom(String name, Long parentId);
}