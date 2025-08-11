package com.example.mycertificationexperience.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CertificationExperience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // من كتب التجربة
    @NotNull(message = "Contributor ID is required")
    @Column(nullable = false)
    private Integer contributorId;

    // تصنيف الشهادة
    @NotNull(message = "Category ID is required")
    @Column(nullable = false)
    private Integer categoryId;

    // معلومات الشهادة (بديل للـ Certificate)
    @NotEmpty(message = "Certificate name cannot be empty")
    @Column(nullable = false)
    private String certificateName;

    @NotEmpty(message = "Provider cannot be empty")
    @Column(nullable = false)
    private String provider;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be 0 or higher")
    @Column(nullable = false)
    private Double price;

    @NotNull(message = "Question count is required")
    @Min(value = 1, message = "There must be at least 1 question")
    @Column(nullable = false)
    private Integer questionCount;

    @NotEmpty(message = "Certificate description cannot be empty")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String certificateDescription;

    // تفاصيل التجربة
    @NotNull(message = "Score is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Score must be 0 or higher")
    @DecimalMax(value = "100.0", inclusive = true, message = "Score cannot exceed 100")
    @Column(nullable = false)
    private Double scoreAchieved;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @Column(nullable = false)
    private Integer rating;

    @NotEmpty(message = "Experience description cannot be empty")
    @Size(min = 10, message = "Experience description must be at least 10 characters")
    @Column(columnDefinition = "TEXT" , nullable = false)
    private String experienceDescription;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}