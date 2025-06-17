package ru.gb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gb.model.Category;
import ru.gb.repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category findOrCreateCategory(String categoryPath) {
        if (categoryPath == null || categoryPath.trim().isEmpty()) {
            return null;
        }
        String[] parts = categoryPath.split(":");
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
}