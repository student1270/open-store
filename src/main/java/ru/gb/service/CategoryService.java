package ru.gb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gb.model.Category;
import ru.gb.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category findOrCreateCategory(String categoryPath) {
        if (categoryPath == null || categoryPath.trim().isEmpty()) {
            return null;
        }
        String[] parts = categoryPath.split(":"); // Masalan, parts = ["elektronika", "smartfonlar va telefonlar", "Smartfonlar"];
        Category current = null;
        for (String part : parts) {
            Long parentId = current != null ? current.getId() : null;
            Category existing = categoryRepository.findByCategoryNameAndParentIdCustom(part, parentId)
                    .orElse(null);
            if (existing == null) {
                Category newCategory = new Category();
                newCategory.setCategoryName(part);
                newCategory.setParent(current);
                categoryRepository.save(newCategory);
                current = newCategory;
            } else {
                current = existing;
            }
        }
        return current;
    }

    public Category findByNameAndParent(String name, Category parent) {
        Long parentId = parent != null ? parent.getId() : null;
        return categoryRepository.findByCategoryNameAndParentIdCustom(name, parentId)
                .orElse(null);
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }
}