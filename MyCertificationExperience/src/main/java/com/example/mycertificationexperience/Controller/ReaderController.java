package com.example.mycertificationexperience.Controller;

import com.example.mycertificationexperience.Api.ApiResponse;
import com.example.mycertificationexperience.Model.Reader;
import com.example.mycertificationexperience.Service.ReaderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/READER")
@RequiredArgsConstructor

public class ReaderController {
    private final ReaderService readerService;


    @PostMapping("/ADD")
    public ResponseEntity<?> addReader(@Valid @RequestBody Reader reader, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError().getDefaultMessage());
        }
        readerService.addReader(reader);
        return ResponseEntity.ok().body(new ApiResponse("Reader successfully added"));
    }
}
