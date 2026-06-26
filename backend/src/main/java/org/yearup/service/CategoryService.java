package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.Category;
import org.yearup.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getById(int categoryId) {
        return categoryRepository.findById(categoryId).orElse(null);
    }

    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    public Category update(int categoryId, Category category) {
        Category current = categoryRepository.findById(categoryId).orElse(null);
        if (current == null) {
            return null;
        }
        current.setCategoryId(categoryId);
        current.setName(category.getName());
        current.setDescription(category.getDescription());
        return categoryRepository.save(current);
    }

    public void delete(int categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
