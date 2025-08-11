package com.example.mycertificationexperience.Service;

import com.example.mycertificationexperience.Api.ApiException;
import com.example.mycertificationexperience.Model.Contributor;
import com.example.mycertificationexperience.Model.ZoomSession;
import com.example.mycertificationexperience.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ZoomSessionService {

    private final ZoomSessionRepository zoomSessionRepository;
    private final ContributorRepository contributorRepository;
    private final BookingRepository bookingRepository;

    public List<ZoomSession> getAll() {
        return zoomSessionRepository.findAll();
    }

    public ZoomSession getOne(Integer id) {
        ZoomSession s = zoomSessionRepository.findZoomSessionById(id);
        if (s == null) throw new ApiException("Session not found");
        return s;
    }

    public List<ZoomSession> getByContributor(Integer contributorId) {
        Contributor c = contributorRepository.findContributorById(contributorId);
        if (c == null) throw new ApiException("Contributor not found");
        return zoomSessionRepository.findAllByContributorId(contributorId);
    }

    public List<ZoomSession> getAvailableUpcoming() {
        return zoomSessionRepository.findAllByIsAvailableTrueAndStartTimeAfter(LocalDateTime.now());
    }

    // CREATE: جلسة واحدة لكل تجربة + وقت مستقبلي (+ساعتين) + المالك Contributor


    @Transactional
    public void add(Integer contributorId, ZoomSession payload) {
        Contributor c = contributorRepository.findContributorById(contributorId);
        if (c == null) throw new ApiException("Contributor not found");

        if (payload.getStartTime() == null || !payload.getStartTime().isAfter(LocalDateTime.now()))
            throw new ApiException("Start time must be in the future");

        if (payload.getMeetingId() == null) throw new ApiException("Meeting ID is required");
        if (zoomSessionRepository.existsByMeetingId(payload.getMeetingId()))
            throw new ApiException("Meeting ID already exists");

        payload.setContributorId(contributorId);
        if (payload.getIsAvailable() == null) payload.setIsAvailable(true);

        zoomSessionRepository.save(payload);
    }


    @Transactional
    public void update(Integer contributorId, Integer sessionId, ZoomSession updated) {
        // تحقق المساهم
        Contributor c = contributorRepository.findContributorById(contributorId);
        if (c == null) throw new ApiException("Contributor not found");

        ZoomSession existing = zoomSessionRepository.findZoomSessionById(sessionId);
        if (existing == null) throw new ApiException("Session not found");
        if (!existing.getContributorId().equals(contributorId)) {
            throw new ApiException("You can only update your own sessions");
        }

        // ممنوع التعديل إذا بدأت أو كانت محجوزة
        if (!existing.getStartTime().isAfter(LocalDateTime.now()))
            throw new ApiException("Cannot update a session that already started");
        if (bookingRepository.existsByZoomSessionId(sessionId))
            throw new ApiException("Cannot update a booked session");

        // تغييرات مسموحة
        if (updated.getPrice() != null) {
            if (updated.getPrice() <= 0) throw new ApiException("Price must be greater than 0");
            existing.setPrice(updated.getPrice());
        }
        if (updated.getBookingLink() != null) existing.setBookingLink(updated.getBookingLink());
        if (updated.getDurationMinutes() != null) {
            if (updated.getDurationMinutes() < 1) throw new ApiException("Duration must be at least 1 minute");
            existing.setDurationMinutes(updated.getDurationMinutes());
        }
        if (updated.getHostLink() != null) existing.setHostLink(updated.getHostLink());

        if (updated.getStartTime() != null) {
            if (!updated.getStartTime().isAfter(LocalDateTime.now()))
                throw new ApiException("Start time must be in the future");
            existing.setStartTime(updated.getStartTime());
        }

        if (updated.getMeetingId() != null && !updated.getMeetingId().equals(existing.getMeetingId())) {
            if (zoomSessionRepository.existsByMeetingId(updated.getMeetingId()))
                throw new ApiException("Meeting ID already exists");
            existing.setMeetingId(updated.getMeetingId());
        }

        // تعطيل/تمكين يدوي قبل الحجز فقط
        if (updated.getIsAvailable() != null) {
            existing.setIsAvailable(updated.getIsAvailable());
        }

        zoomSessionRepository.save(existing);
    }


    // ==================== Delete ====================

    @Transactional
    public void delete(Integer contributorId, Integer sessionId) {
        // تحقق المساهم
        Contributor c = contributorRepository.findContributorById(contributorId);
        if (c == null) throw new ApiException("Contributor not found");

        ZoomSession existing = zoomSessionRepository.findZoomSessionById(sessionId);
        if (existing == null) throw new ApiException("Session not found");
        if (!existing.getContributorId().equals(contributorId)) {
            throw new ApiException("You can only delete your own sessions");
        }

        if (!existing.getStartTime().isAfter(LocalDateTime.now()))
            throw new ApiException("Cannot delete a session that already started");

        // لو محجوزة نمنع الحذف (اللغاء يتم من Booking)
        if (bookingRepository.existsByZoomSessionId(sessionId)) {
            throw new ApiException("Cannot delete a booked session");
        }


        zoomSessionRepository.delete(existing);
    }

    // ==================== Toggle Availability ====================

    @Transactional
    public void toggleAvailability(Integer contributorId, Integer sessionId, boolean available) {
        // تحقق المساهم
        Contributor c = contributorRepository.findContributorById(contributorId);
        if (c == null) throw new ApiException("Contributor not found");

        ZoomSession s = zoomSessionRepository.findZoomSessionById(sessionId);
        if (s == null) throw new ApiException("Session not found");
        if (!s.getContributorId().equals(contributorId)) {
            throw new ApiException("You can only modify your own session");
        }

        LocalDateTime now = LocalDateTime.now();

        // 1) منع أي تغيير بعد بدء الجلسة
        if (!s.getStartTime().isAfter(now)) {
            throw new ApiException("Cannot change availability after the session has started");
        }

        // 2) مهلة قبل البدء (ساعتان) — تنطبق عند التفعيل فقط
        int bufferHours = 2;
        if (available && !s.getStartTime().isAfter(now.plusHours(bufferHours))) {
            throw new ApiException("Cannot enable availability less than 2 hours before start");
        }

        // 3) Idempotent: لا تغيّر إذا الحالة نفسها
        if (Boolean.TRUE.equals(s.getIsAvailable()) == available) return;

        // 4) عند التفعيل لازم ما يكون فيه حجز
        if (available && bookingRepository.existsByZoomSessionId(sessionId)) {
            throw new ApiException("Cannot set available=true while bookings exist");
        }

        s.setIsAvailable(available);
        zoomSessionRepository.save(s);
    }


}
