package com.example.mycertificationexperience.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // نربط التقييم بحجز فعلي لضمان إن المراجع حضر الجلسة
    @NotNull(message = "Booking ID is required")
    @Column(nullable = false, unique = true)
    private Integer bookingId;

    // صاحب المراجعة (القارئ)
    @NotNull(message = "Reviewer (reader) ID is required")
    @Column(nullable = false)
    private Integer readerId;

    // تقييم من 1 إلى 5
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @Column(nullable = false)
    private Integer rating;

    // تعليق اختياري لكن لو انكتب يكون بطول معقول
    @Size(min = 5, max = 2000, message = "Comment must be between 5 and 2000 characters")
    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
