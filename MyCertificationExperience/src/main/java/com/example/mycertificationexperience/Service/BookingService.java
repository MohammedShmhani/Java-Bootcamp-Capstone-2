package com.example.mycertificationexperience.Service;

import com.example.mycertificationexperience.Api.ApiException;
import com.example.mycertificationexperience.Model.Booking;
import com.example.mycertificationexperience.Model.Reader;
import com.example.mycertificationexperience.Model.ZoomSession;
import com.example.mycertificationexperience.Repository.BookingRepository;
import com.example.mycertificationexperience.Repository.ReaderRepository;
import com.example.mycertificationexperience.Repository.ZoomSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor

public class BookingService {
    private final BookingRepository bookingRepository;
    private final ReaderRepository readerRepository;   // بدل UserRepository
    private final ZoomSessionRepository zoomSessionRepository; // اسم موحّد

    @Transactional
    public void addBooking(Integer readerId, Integer zoomSessionId) {
        // 1) تأكد أن القارئ موجود
        Reader reader = readerRepository.findReaderById(readerId);
        if (reader == null) throw new ApiException("Reader not found");

        // 2) الجلسة موجودة ومُتاحة ولم يبدأ وقتها
        ZoomSession session = zoomSessionRepository.findZoomSessionById(zoomSessionId);
        if (session == null) throw new ApiException("Session not found");
        if (!Boolean.TRUE.equals(session.getIsAvailable())) throw new ApiException("Session is not available");
        if (session.getStartTime().isBefore(LocalDateTime.now())) throw new ApiException("Session already started");

        // 3) ممنوع حجز نفس الجلسة لنفس القارئ
        if (bookingRepository.existsByReaderIdAndZoomSessionId(readerId, zoomSessionId)) {
            throw new ApiException("You already booked this session");
        }

        // 4) تحقق الرصيد وكفايته
        Double price = session.getPrice();
        if (reader.getWalletBalance() < price) throw new ApiException("Insufficient wallet balance");

        // 5) خصم المبلغ
        reader.setWalletBalance(reader.getWalletBalance() - price);
        readerRepository.save(reader);

        // 6) إنشاء الحجز (bookedAt يُعبأ تلقائياً بـ @CreationTimestamp)
        Booking booking = new Booking();
        booking.setReaderId(readerId);
        booking.setZoomSessionId(zoomSessionId);
        booking.setIsConfirmed(true); // حسب اختيارك boolean

        bookingRepository.save(booking);

        // 7) بما أن الجلسة فردية: عطّلها بعد التأكيد
        session.setIsAvailable(false);
        // لو عندك حقل bookingId في ZoomSession فعّل السطر التالي:
        // session.setBookingId(booking.getId());
        zoomSessionRepository.save(session);
    }


    @Transactional
    public void deleteBooking(Integer readerId, Integer bookingId) {
        // 1) تأكد أن القارئ موجود
        Reader reader = readerRepository.findReaderById(readerId);
        if (reader == null) throw new ApiException("Reader not found");

        // 2) تأكد أن الحجز موجود
        Booking booking = bookingRepository.findBookingById(bookingId);
        if (booking == null) throw new ApiException("Booking not found");

        // 3) تأكد أن الحجز يخص هذا القارئ
        if (!booking.getReaderId().equals(readerId)) {
            throw new ApiException("You can only delete your own booking");
        }

        // 4) جب الجلسة وتأكد أنها ما بدأت
        ZoomSession session = zoomSessionRepository.findZoomSessionById(booking.getZoomSessionId());
        if (session == null) throw new ApiException("Session not found");
        if (session.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ApiException("Cannot delete booking after the session has started");
        }

        // 5) احذف الحجز وأعد الجلسة كمتاحة (فردية)
        bookingRepository.delete(booking);
        session.setIsAvailable(true);
        // لو عندك bookingId داخل ZoomSession:
        // session.setBookingId(null);
        zoomSessionRepository.save(session);
    }

    public List<Booking> getAllBookings(Integer readerId) {
        // تأكد أن القارئ موجود
        Reader reader = readerRepository.findReaderById(readerId);
        if (reader == null) {
            throw new ApiException("Reader not found");
        }

        // رجّع حجوزات القارئ فقط
        return bookingRepository.findByReaderId(readerId);
    }


    @Transactional
    public void updateBooking(Integer readerId, Integer bookingId, Booking updatedBooking) {
        // 1) القارئ موجود
        Reader reader = readerRepository.findReaderById(readerId);
        if (reader == null) throw new ApiException("Reader not found");

        // 2) الحجز موجود
        Booking existing = bookingRepository.findBookingById(bookingId);
        if (existing == null) throw new ApiException("Booking not found");

        // 3) الحجز يخص هذا القارئ
        if (!existing.getReaderId().equals(readerId)) {
            throw new ApiException("You can only update your own booking");
        }

        // 4) الجلسة الحالية موجودة ولم تبدأ
        ZoomSession currentSession = zoomSessionRepository.findZoomSessionById(existing.getZoomSessionId());
        if (currentSession == null) throw new ApiException("Session not found");
        if (currentSession.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ApiException("Cannot update booking after the session has started");
        }

        // 5) تحديث isConfirmed (اختياري)
        if (updatedBooking.getIsConfirmed() != null) {
            existing.setIsConfirmed(updatedBooking.getIsConfirmed());
            // لو تم التأكيد خلي الجلسة غير متاحة
            if (Boolean.TRUE.equals(updatedBooking.getIsConfirmed())) {
                currentSession.setIsAvailable(false);
            } else {
                // لو أُلغي التأكيد، رجّعها متاحة (بما أنها فردية)
                currentSession.setIsAvailable(true);
            }
            zoomSessionRepository.save(currentSession);
        }

        // 6) تبديل الجلسة (إن طُلب)
        Integer newSessionId = updatedBooking.getZoomSessionId();
        if (newSessionId != null && !newSessionId.equals(existing.getZoomSessionId())) {
            // الجلسة الجديدة موجودة ومُتاحة ولم تبدأ
            ZoomSession newSession = zoomSessionRepository.findZoomSessionById(newSessionId);
            if (newSession == null) throw new ApiException("New session not found");
            if (!Boolean.TRUE.equals(newSession.getIsAvailable())) throw new ApiException("New session is not available");
            if (newSession.getStartTime().isBefore(LocalDateTime.now())) throw new ApiException("New session already started");

            // القارئ ما عنده حجز مسبق لنفس الجلسة
            if (bookingRepository.existsByReaderIdAndZoomSessionId(readerId, newSessionId)) {
                throw new ApiException("You already booked this new session");
            }

            // فرّغ القديمة لأن الجلسة فردية
            currentSession.setIsAvailable(true);
            zoomSessionRepository.save(currentSession);

            // اربط الجديدة واغلقها إن كان الحجز مؤكداً
            existing.setZoomSessionId(newSessionId);
            if (Boolean.TRUE.equals(existing.getIsConfirmed())) {
                newSession.setIsAvailable(false);
            }
            zoomSessionRepository.save(newSession);
        }

        bookingRepository.save(existing);
    }

    public Map<String, Object> canBook(Integer readerId, Integer zoomSessionId) {
        Map<String, Object> res = new HashMap<>();

        // 1) Reader exists
        Reader reader = readerRepository.findReaderById(readerId);
        if (reader == null) {
            res.put("canBook", false);
            res.put("code", "READER_NOT_FOUND");
            res.put("message", "Reader not found");
            return res;
        }

        // 2) Session exists
        ZoomSession session = zoomSessionRepository.findZoomSessionById(zoomSessionId);
        if (session == null) {
            res.put("canBook", false);
            res.put("code", "SESSION_NOT_FOUND");
            res.put("message", "Session not found");
            return res;
        }

        // 3) Session not started
        if (session.getStartTime().isBefore(LocalDateTime.now())) {
            res.put("canBook", false);
            res.put("code", "SESSION_ALREADY_STARTED");
            res.put("message", "Session already started");
            return res;
        }

        // 4) Session available
        if (!Boolean.TRUE.equals(session.getIsAvailable())) {
            res.put("canBook", false);
            res.put("code", "SESSION_NOT_AVAILABLE");
            res.put("message", "Session is not available");
            return res;
        }

        // 5) Not already booked by this reader
        if (bookingRepository.existsByReaderIdAndZoomSessionId(readerId, zoomSessionId)) {
            res.put("canBook", false);
            res.put("code", "ALREADY_BOOKED_BY_READER");
            res.put("message", "You already booked this session");
            return res;
        }

        // 6) (احتياطي) الجلسة مو محجوزة لشخص آخر
        if (bookingRepository.existsByZoomSessionId(zoomSessionId)) {
            res.put("canBook", false);
            res.put("code", "SESSION_ALREADY_BOOKED");
            res.put("message", "This session has already been booked");
            return res;
        }

        // 7) Wallet balance sufficient
        double price = session.getPrice();
        if (reader.getWalletBalance() < price) {
            res.put("canBook", false);
            res.put("code", "INSUFFICIENT_BALANCE");
            res.put("message", "Insufficient wallet balance");
            res.put("price", price);
            res.put("walletBalance", reader.getWalletBalance());
            return res;
        }

        // OK ✅
        res.put("canBook", true);
        res.put("price", price);
        res.put("walletBalance", reader.getWalletBalance());
        return res;
    }


}


