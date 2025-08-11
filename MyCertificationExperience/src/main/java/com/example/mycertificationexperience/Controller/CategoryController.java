package com.example.mycertificationexperience.Controller;

import com.example.mycertificationexperience.Api.ApiResponse;
import com.example.mycertificationexperience.Model.Category;
import com.example.mycertificationexperience.Service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/CATEGORY")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/GET")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategories());
    }

    @PostMapping("/ADD/{adminId}")
    public ResponseEntity<?> addCategory(@PathVariable Integer adminId,
                                         @Valid @RequestBody Category category,
                                         Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError().getDefaultMessage());
        }
        categoryService.addCategory(adminId, category);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Successfully added Category"));
    }

    @DeleteMapping("/DELETE/{adminId}/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer adminId,
                                            @PathVariable Integer id) {
        categoryService.deleteCategory(adminId, id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Successfully deleted Category"));
    }

    @PutMapping("/UPDATE/{adminId}/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer adminId,
                                            @PathVariable Integer id,
                                            @Valid @RequestBody Category category,
                                            Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError().getDefaultMessage());
        }
        categoryService.updateCategory(adminId, id, category);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Successfully updated Category"));
    }
}
