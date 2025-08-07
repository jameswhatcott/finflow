package com.jameswhatcott.finance.personal_finance_tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jameswhatcott.finance.personal_finance_tracker.entity.Category;
import com.jameswhatcott.finance.personal_finance_tracker.repository.CategoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    // Create a new category
    public Category createCategory(Category category) {
        // Business validation
        validateCategory(category);
        
        // Set timestamps
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        
        return categoryRepository.save(category);
    }
    
    // Get all categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    // Get category by ID
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // Get category by name
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
    
    // Update category by ID
    public Category updateCategory(Long id, Category categoryDetails) {
        Category existingCategory = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        // Update fields
        existingCategory.setName(categoryDetails.getName());
        existingCategory.setType(categoryDetails.getType());
        existingCategory.setColor(categoryDetails.getColor());
        existingCategory.setUpdatedAt(LocalDateTime.now());
        
        return categoryRepository.save(existingCategory);
    }

    // Update category (for internal use)
    public Category updateCategory(Category category) {
        category.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }
    
    // Delete category
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
    
    // Business validation
    private void validateCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new RuntimeException("Category name is required");
        }
        
        // Check if category name already exists
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new RuntimeException("Category name already exists");
        }
    }
}
