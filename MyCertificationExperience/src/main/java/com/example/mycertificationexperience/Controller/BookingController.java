package com.example.mycertificationexperience.Controller;

import com.example.mycertificationexperience.Api.ApiResponse;
import com.example.mycertificationexperience.Model.Booking;
import com.example.mycertificationexperience.Service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/BOOKING")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;


    @GetMapping("/GET/{readerId}")
    public ResponseEntity<?> getBookings(@PathVariable Integer readerId) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getAllBookings(readerId));
    }

    @PostMapping("/ADD/{readerId}/{zoomSessionId}")
    public ResponseEntity<?> addBooking(@PathVariable Integer readerId,
                                        @PathVariable Integer zoomSessionId) {

        bookingService.addBooking(readerId, zoomSessionId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Booking added successfully"));
    }

    @DeleteMapping("/DELETE/{readerId}/{bookingId}")
    public ResponseEntity<?> deleteBooking(@PathVariable Integer readerId,
                                           @PathVariable Integer bookingId) {
        bookingService.deleteBooking(readerId, bookingId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Booking removed successfully"));
    }



    @PutMapping("/UPDATE/{readerId}/{bookingId}")
    public ResponseEntity<?> updateBooking(@PathVariable Integer readerId,
                                           @PathVariable Integer bookingId,
                                           @Valid @RequestBody Booking booking,
                                           Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors.getFieldError().getDefaultMessage());
        }
        bookingService.updateBooking(readerId, bookingId, booking);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Booking updated successfully"));
    }

    // BookingController.java
    @GetMapping("/CAN-BOOK/{readerId}/{zoomSessionId}")
    public ResponseEntity<?> canBook(@PathVariable Integer readerId,
                                     @PathVariable Integer zoomSessionId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookingService.canBook(readerId, zoomSessionId));
    }


}
