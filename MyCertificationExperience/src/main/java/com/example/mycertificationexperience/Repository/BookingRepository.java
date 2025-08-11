package com.example.mycertificationexperience.Repository;

import com.example.mycertificationexperience.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {


    Booking findBookingById(Integer bookingId);

    // رجّع حجوزات القارئ
    List<Booking> findByReaderId(Integer readerId);

    // منع حجز نفس الجلسة لنفس القارئ
    boolean existsByReaderIdAndZoomSessionId(Integer readerId, Integer zoomSessionId);

    // يفيد عند حماية حذف/تعديل الجلسة
    boolean existsByZoomSessionId(Integer zoomSessionId);

    // + in BookingRepository

    List<Booking> findAllByZoomSessionIdIn(List<Integer> zoomSessionIds);

    List<Booking> findAllByZoomSessionId(Integer zoomSessionId);


}

