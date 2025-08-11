package com.example.mycertificationexperience.Service;

import com.example.mycertificationexperience.Api.ApiException;
import com.example.mycertificationexperience.Model.Booking;
import com.example.mycertificationexperience.Model.Reader;
import com.example.mycertificationexperience.Model.Review;
import com.example.mycertificationexperience.Model.ZoomSession;
import com.example.mycertificationexperience.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor

public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final ReaderRepository readerRepository;
    private final ZoomSessionRepository zoomSessionRepository;
    private final ContributorRepository contributorRepository;


    // ========= READ =========
    public Review getOne(Integer reviewId) {
        Review r = reviewRepository.findReviewById(reviewId);
        if (r == null) throw new ApiException("Review not found");
        return r;
    }

    public List<Review> getAllByReader(Integer readerId) {
        Reader reader = readerRepository.findReaderById(readerId);
        if (reader == null) throw new ApiException("Reader not found");
        return reviewRepository.findAllByReaderId(readerId);
    }

    // ========= CREATE =========
    @Transactional
    public void add(Integer readerId, Integer bookingId, Review payload) {
        // 1) تحقق القارئ والحجز والملكية
        Reader reader = readerRepository.findReaderById(readerId);
        if (reader == null) throw new ApiException("Reader not found");

        Booking booking = bookingRepository.findBookingById(bookingId);
        if (booking == null) throw new ApiException("Booking not found");
        if (!booking.getReaderId().equals(readerId))
            throw new ApiException("You can only review your own booking");
        if (!Boolean.TRUE.equals(booking.getIsConfirmed()))
            throw new ApiException("Booking is not confirmed");

        // 2) جلسة الحجز موجودة وانتهى وقتها
        ZoomSession session = zoomSessionRepository.findZoomSessionById(booking.getZoomSessionId());
        if (session == null) throw new ApiException("Session not found");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sessionEnd = session.getStartTime().plusMinutes(session.getDurationMinutes());
        if (now.isBefore(sessionEnd))
            throw new ApiException("You can only review after the session ends");

        // 3) منع تكرار المراجعة لنفس الحجز
        if (reviewRepository.existsByBookingId(bookingId))
            throw new ApiException("A review already exists for this booking");

        // 4) حفظ المراجعة
        Review r = new Review();
        r.setBookingId(bookingId);
        r.setReaderId(readerId);
        r.setRating(payload.getRating());
        r.setComment(payload.getComment() == null ? null : payload.getComment().trim());

        reviewRepository.save(r);
    }

    // ========= UPDATE =========
    @Transactional
    public void update(Integer readerId, Integer reviewId, Review updated) {
        // 1) تحقق القارئ والمراجعة والملكية
        Reader reader = readerRepository.findReaderById(readerId);
        if (reader == null) throw new ApiException("Reader not found");

        Review existing = reviewRepository.findReviewById(reviewId);
        if (existing == null) throw new ApiException("Review not found");
        if (!existing.getReaderId().equals(readerId))
            throw new ApiException("You can only update your own review");

        // 2) تحديث الحقول المسموحة
        if (updated.getRating() != null) existing.setRating(updated.getRating());
        if (updated.getComment() != null) existing.setComment(updated.getComment().trim());

        reviewRepository.save(existing);
    }

    // ========= DELETE =========
    @Transactional
    public void delete(Integer readerId, Integer reviewId) {
        Reader reader = readerRepository.findReaderById(readerId);
        if (reader == null) throw new ApiException("Reader not found");

        Review existing = reviewRepository.findReviewById(reviewId);
        if (existing == null) throw new ApiException("Review not found");
        if (!existing.getReaderId().equals(readerId))
            throw new ApiException("You can only delete your own review");

        reviewRepository.delete(existing);
    }




    public List<Review> getAllByContributor(Integer contributorId) {
        // 1) تحقق من وجود المساهم
        if (contributorRepository.findContributorById(contributorId) == null) {
            throw new ApiException("Contributor not found");
        }

        // 2) جلسات المساهم
        List<ZoomSession> sessions = zoomSessionRepository.findAllByContributorId(contributorId);
        if (sessions == null || sessions.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        // 3) جمع معرّفات الجلسات
        List<Integer> sessionIds = new java.util.ArrayList<>();
        for (ZoomSession s : sessions) {
            sessionIds.add(s.getId());
        }

        // 4) الحجوزات لتلك الجلسات
        List<Booking> bookings = bookingRepository.findAllByZoomSessionIdIn(sessionIds);
        if (bookings == null || bookings.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        // 5) جمع معرّفات الحجوزات
        List<Integer> bookingIds = new java.util.ArrayList<>();
        for (Booking b : bookings) {
            bookingIds.add(b.getId());
        }

        // 6) مراجعات تلك الحجوزات
        return reviewRepository.findAllByBookingIdIn(bookingIds);
    }


    public Map<String, Object> getSummaryBySession(Integer sessionId) {
        // تأكد أن الجلسة موجودة
        if (zoomSessionRepository.findZoomSessionById(sessionId) == null) {
            throw new ApiException("Session not found");
        }

        // حجوزات الجلسة
        List<Booking> bookings = bookingRepository.findAllByZoomSessionId(sessionId);
        if (bookings == null || bookings.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("averageRating", 0.0);
            empty.put("count", 0);
            return empty;
        }

        // ids الحجوزات
        List<Integer> bookingIds = new java.util.ArrayList<>();
        for (Booking b : bookings) {
            bookingIds.add(b.getId());
        }

        // المراجعات
        List<Review> reviews = reviewRepository.findAllByBookingIdIn(bookingIds);
        int count = (reviews == null) ? 0 : reviews.size();

        double avg = 0.0;
        if (count > 0) {
            int sum = 0;
            for (Review r : reviews) {
                sum += (r.getRating() == null ? 0 : r.getRating());
            }
            avg = sum / (double) count;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("averageRating", avg);
        result.put("count", count);
        return result;
    }


}
