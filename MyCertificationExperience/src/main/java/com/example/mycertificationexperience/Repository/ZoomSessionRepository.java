package com.example.mycertificationexperience.Repository;

import com.example.mycertificationexperience.Model.ZoomSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ZoomSessionRepository extends JpaRepository<ZoomSession, Integer> {
    ZoomSession findZoomSessionById(Integer id);

    List<ZoomSession> findAllByContributorId(Integer contributorId);

    List<ZoomSession> findAllByIsAvailableTrueAndStartTimeAfter(LocalDateTime startTime);

    boolean existsByMeetingId(Long meetingId);


}
