package com.example.mycertificationexperience.Controller;

import com.example.mycertificationexperience.Api.ApiResponse;
import com.example.mycertificationexperience.Model.Review;
import com.example.mycertificationexperience.Service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/REVIEW")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;



    // GET one by id
    @GetMapping("/GET/{reviewId}")
    public ResponseEntity<?> getOne(@PathVariable Integer reviewId) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getOne(reviewId));
    }

    // GET all by reader
    @GetMapping("/GET/BY-READER/{readerId}")
    public ResponseEntity<?> getAllByReader(@PathVariable Integer readerId) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getAllByReader(readerId));
    }

    // ADD (reader creates review for a booking)
    @PostMapping("/ADD/{readerId}/{bookingId}")
    public ResponseEntity<?> add(@PathVariable Integer readerId,
                                 @PathVariable Integer bookingId,
                                 @Valid @RequestBody Review review,
                                 Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError().getDefaultMessage());
        }
        reviewService.add(readerId, bookingId, review);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Review added successfully"));
    }

    // UPDATE (only owner reader)
    @PutMapping("/UPDATE/{readerId}/{reviewId}")
    public ResponseEntity<?> update(@PathVariable Integer readerId,
                                    @PathVariable Integer reviewId,
                                    @Valid @RequestBody Review review,
                                    Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError().getDefaultMessage());
        }
        reviewService.update(readerId, reviewId, review);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Review updated successfully"));
    }

    // DELETE (only owner reader)
    @DeleteMapping("/DELETE/{readerId}/{reviewId}")
    public ResponseEntity<?> delete(@PathVariable Integer readerId,
                                    @PathVariable Integer reviewId) {
        reviewService.delete(readerId, reviewId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Review deleted successfully"));
    }

    @GetMapping("/GET/BY-CONTRIBUTOR/{contributorId}")
    public ResponseEntity<?> getByContributor(@PathVariable Integer contributorId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(reviewService.getAllByContributor(contributorId));
    }


    // + in ReviewController
    @GetMapping("/SUMMARY/SESSION/{sessionId}")
    public ResponseEntity<?> getSessionReviewSummary(@PathVariable Integer sessionId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(reviewService.getSummaryBySession(sessionId));
    }




}
