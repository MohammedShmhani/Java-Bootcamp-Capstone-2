package com.example.mycertificationexperience.Repository;

import com.example.mycertificationexperience.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByName(String name);

    Category findCategoryById(Integer categoryId);
}
