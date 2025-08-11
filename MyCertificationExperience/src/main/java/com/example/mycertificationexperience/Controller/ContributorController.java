package com.example.mycertificationexperience.Controller;

import com.example.mycertificationexperience.Api.ApiResponse;
import com.example.mycertificationexperience.Model.Contributor;
import com.example.mycertificationexperience.Service.ContributerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/CONTRIBUTOR")
@RequiredArgsConstructor
public class ContributorController {

    private final ContributerService  contributerService;

    @PostMapping("/ADD")
    public ResponseEntity<?>  addContributor(@Valid @RequestBody Contributor contributor, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError().getDefaultMessage());
        }
        contributerService.addContributor(contributor);
        return ResponseEntity.ok().body(new ApiResponse("Contributor added successfully"));

    }

}
