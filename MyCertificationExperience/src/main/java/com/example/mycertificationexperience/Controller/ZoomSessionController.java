package com.example.mycertificationexperience.Controller;

import com.example.mycertificationexperience.Api.ApiResponse;
import com.example.mycertificationexperience.Model.ZoomSession;
import com.example.mycertificationexperience.Service.ZoomSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/SESSION")
@RequiredArgsConstructor
public class ZoomSessionController {

    private final ZoomSessionService zoomSessionService;


    // GET all
    @GetMapping("/GET")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(zoomSessionService.getAll());
    }

    // GET one by id
    @GetMapping("/GET/{sessionId}")
    public ResponseEntity<?> getOne(@PathVariable Integer sessionId) {
        return ResponseEntity.status(HttpStatus.OK).body(zoomSessionService.getOne(sessionId));
    }

    // GET available upcoming sessions
    @GetMapping("/GET/AVAILABLE")
    public ResponseEntity<?> getAvailableUpcoming() {
        return ResponseEntity.status(HttpStatus.OK).body(zoomSessionService.getAvailableUpcoming());
    }

    // GET sessions by contributor
    @GetMapping("/GET/BY-CONTRIBUTOR/{contributorId}")
    public ResponseEntity<?> getByContributor(@PathVariable Integer contributorId) {
        return ResponseEntity.status(HttpStatus.OK).body(zoomSessionService.getByContributor(contributorId));
    }

    // ADD (CONTRIBUTOR)
    @PostMapping("/ADD/{contributorId}")
    public ResponseEntity<?> add(@PathVariable Integer contributorId,
                                 @Valid @RequestBody ZoomSession session,
                                 Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError().getDefaultMessage());
        }
        zoomSessionService.add(contributorId, session);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Successfully added Session"));
    }

    // UPDATE (CONTRIBUTOR) — ممنوع بعد البدء أو إذا كانت محجوزة
    @PutMapping("/UPDATE/{contributorId}/{sessionId}")
    public ResponseEntity<?> update(@PathVariable Integer contributorId,
                                    @PathVariable Integer sessionId,
                                    @Valid @RequestBody ZoomSession session,
                                    Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError().getDefaultMessage());
        }
        zoomSessionService.update(contributorId, sessionId, session);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Successfully updated Session"));
    }

    // DELETE (قبل البدء وبدون حجوزات)
    @DeleteMapping("/DELETE/{contributorId}/{sessionId}")
    public ResponseEntity<?> delete(@PathVariable Integer contributorId,
                                    @PathVariable Integer sessionId) {
        zoomSessionService.delete(contributorId, sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Successfully deleted Session"));
    }

    // Toggle availability (إتاحة/إيقاف)
    @PutMapping("/TOGGLE/{contributorId}/{sessionId}/{available}")
    public ResponseEntity<?> toggleAvailability(@PathVariable Integer contributorId,
                                                @PathVariable Integer sessionId,
                                                @PathVariable boolean available) {
        zoomSessionService.toggleAvailability(contributorId, sessionId, available);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Availability updated"));
    }

}
