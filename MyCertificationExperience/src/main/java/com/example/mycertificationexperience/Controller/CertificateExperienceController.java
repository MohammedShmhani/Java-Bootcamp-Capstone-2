package com.example.mycertificationexperience.Controller;

import com.example.mycertificationexperience.Api.ApiResponse;
import com.example.mycertificationexperience.Model.CertificationExperience;
import com.example.mycertificationexperience.Service.CertificateExperienceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/CE")
@RequiredArgsConstructor
public class CertificateExperienceController {

    private final CertificateExperienceService certificateExperienceService;

    // GET all
    @GetMapping("/GET")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(certificateExperienceService.getAll());
    }

    // GET one by id
    @GetMapping("/GET/{id}")
    public ResponseEntity<?> getOne(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(certificateExperienceService.getOne(id));
    }

    // ADD (CONTRIBUTOR فقط)
    @PostMapping("/ADD/{contributorId}")
    public ResponseEntity<?> add(@PathVariable Integer contributorId,
                                 @Valid @RequestBody CertificationExperience experience,
                                 Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError().getDefaultMessage());
        }
        certificateExperienceService.add(contributorId, experience);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Successfully added Experience"));
        // لو تبغى REST أنظف: HttpStatus.CREATED بدل OK
    }

    // UPDATE (CONTRIBUTOR وصاحب التجربة)
    @PutMapping("/UPDATE/{contributorId}/{experienceId}")
    public ResponseEntity<?> update(@PathVariable Integer contributorId,
                                    @PathVariable Integer experienceId,
                                    @Valid @RequestBody CertificationExperience experience,
                                    Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError().getDefaultMessage());
        }
        certificateExperienceService.update(contributorId, experienceId, experience);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Successfully updated Experience"));
    }

    // DELETE (CONTRIBUTOR وصاحب التجربة)
    @DeleteMapping("/DELETE/{contributorId}/{experienceId}")
    public ResponseEntity<?> delete(@PathVariable Integer contributorId,
                                    @PathVariable Integer experienceId) {
        certificateExperienceService.delete(contributorId, experienceId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Successfully deleted Experience"));
    }


    // GET /api/v1/experience/by-category/{categoryId}
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<?> getByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(certificateExperienceService.getByCategory(categoryId));
    }


    @GetMapping("/score-above/{score}")
    public ResponseEntity<?> getExperiencesWithScoreAbove(@PathVariable Double score) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(certificateExperienceService.getExperiencesWithScoreAbove(score));
    }

    // CertificationExperienceController (أو كنترولرك الحالي للتجارب)
    @GetMapping("/by-contributor/{contributorId}")
    public ResponseEntity<?> getByContributor(@PathVariable Integer contributorId) {
        return ResponseEntity.ok(certificateExperienceService.getByContributor(contributorId));
    }


    // GET /api/v1/experience/by-provider/{provider}
    @GetMapping("/by-provider/{provider}")
    public ResponseEntity<?> getByProvider(@PathVariable String provider) {
        return ResponseEntity.ok(certificateExperienceService.getByProvider(provider));
    }


    // GET /api/v1/experience/by-price/{min}/{max}
    @GetMapping("/by-price/{min}/{max}")
    public ResponseEntity<?> getByPrice(@PathVariable Double min,
                                        @PathVariable Double max) {
        return ResponseEntity.ok(certificateExperienceService.filterByPrice(min, max));
    }

    // GET /api/v1/experience/by-questions/{min}/{max}
    @GetMapping("/by-questions/{min}/{max}")
    public ResponseEntity<?> getByQuestions(@PathVariable Integer min,
                                            @PathVariable Integer max) {
        return ResponseEntity.ok(certificateExperienceService.filterByQuestionCount(min, max));
    }







}
