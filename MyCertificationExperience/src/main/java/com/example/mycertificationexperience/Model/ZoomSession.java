package com.example.mycertificationexperience.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ZoomSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // صاحب الجلسة (المضيف)
    @NotNull(message = "Contributor ID is required")
    @Min(value = 1, message = "Contributor ID must be greater than 0")
    @Column(nullable = false)
    private Integer contributorId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(nullable = false)
    private Double price;

    // رابط انضمام القارئ
    @NotEmpty(message = "Booking link is required")
    @Column(nullable = false)
    private String bookingLink;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Column(nullable = false)
    private Integer durationMinutes;

    // افتراضيًا متاحة، وتتحول false بعد تأكيد الحجز
    @NotNull(message = "Availability status is required")
    @Column(nullable = false)
    private Boolean isAvailable = true;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    @Column(nullable = false)
    private LocalDateTime startTime;

    // رابط المضيف (لـ Zoom)
    @NotEmpty(message = "Host link is required")
    @Column(nullable = false)
    private String hostLink;

    // معرّف اجتماع Zoom (خليناه Long مثل كودك)
    @NotNull(message = "Meeting ID is required")
    @Column(nullable = false, unique = true)
    private Long meetingId;



}
