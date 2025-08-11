package com.example.mycertificationexperience.Repository;

import com.example.mycertificationexperience.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // جلب مراجعة حسب المعرّف
    Review findReviewById(Integer id);

    // لأن bookingId فريد، فبيرجع عنصر واحد فقط (أو null)
    Review findByBookingId(Integer bookingId);

    // منع تكرار المراجعة لنفس الحجز
    boolean existsByBookingId(Integer bookingId);

    // كل مراجعات قارئ معيّن
    List<Review> findAllByReaderId(Integer readerId);


    List<Review> findAllByBookingIdIn(List<Integer> bookingIds);



    // مراجعات بتقييم أعلى/يساوي قيمة معيّنة
    List<Review> findAllByRatingGreaterThanEqual(Integer rating);

    // (اختياري) مراجعات قارئ معيّن بتقييم أعلى/يساوي قيمة معيّنة
    List<Review> findAllByReaderIdAndRatingGreaterThanEqual(Integer readerId, Integer rating);

    // (اختياري) لمتابعة نشاط حديث: مراجعات بعد وقت معيّن
    List<Review> findAllByCreatedAtAfter(LocalDateTime createdAfter);

    // (اختياري) عدد مراجعات القارئ
    long countByReaderId(Integer readerId);
}
