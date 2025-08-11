package com.example.mycertificationexperience.Service;

import com.example.mycertificationexperience.Api.ApiException;
import com.example.mycertificationexperience.Model.Category;
import com.example.mycertificationexperience.Repository.AdminRepository;
import com.example.mycertificationexperience.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final AdminRepository adminRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    public void deleteCategory(Integer adminId, Integer id) {
        if (!adminRepository.existsById(adminId)) {
            throw new ApiException("Only admins can delete categories");
        }
        Category category1 = categoryRepository.findCategoryById(id);
        if (category1 == null) {
            throw new ApiException("Category not found");
        }
        categoryRepository.delete(category1);
    }

    public void addCategory(Integer adminId, Category category) {
        // تحقق إذا كان الـ admin موجود
        if (!adminRepository.existsById(adminId)) {
            throw new ApiException("Only admins can add categories");
        }

        // تحقق إذا كان الاسم موجود مسبقاً
        if (categoryRepository.existsByName(category.getName())) {
            throw new ApiException("Category name already exists");
        }

        categoryRepository.save(category);
    }

    public void updateCategory(Integer adminId, Integer id, Category category) {
        if (!adminRepository.existsById(adminId)) {
            throw new ApiException("Only admins can update categories");
        }
        Category category1 = categoryRepository.findCategoryById(id);
        if (category1 == null) {
            throw new ApiException("Category not found");
        }
        if (!category1.getName().equals(category.getName()) && categoryRepository.existsByName(category.getName())) {
            throw new ApiException("Category name already exists");
        }
        category1.setName(category.getName());
        categoryRepository.save(category1);
    }

}
