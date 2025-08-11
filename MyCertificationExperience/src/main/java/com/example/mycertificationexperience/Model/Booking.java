package com.example.mycertificationexperience.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Reader ID is required")
    @Column(nullable = false)
    private Integer readerId;
    //ADD ID
    @NotNull(message = "Zoom session ID is required")
    @Column(nullable = false, unique = true)
    private Integer zoomSessionId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime bookedAt;


    @Column(nullable = false)
    private Boolean isConfirmed=false;


}
