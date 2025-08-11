package com.example.mycertificationexperience.Controller;

import com.example.mycertificationexperience.Api.ApiResponse;
import com.example.mycertificationexperience.Model.Admin;
import com.example.mycertificationexperience.Service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ADMIN")
@RequiredArgsConstructor

public class AdminController {
    private final AdminService adminService;

    @PostMapping("/ADD")
    public ResponseEntity<?> addAdmin(@Valid @RequestBody Admin admin, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError().getDefaultMessage());
        }
        adminService.AddAdmin(admin);
        return ResponseEntity.ok().body(new ApiResponse("Admin successfully added"));
    }
}
