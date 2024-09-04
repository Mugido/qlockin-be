package com.decagosq022.qlockin.repository;

import com.decagosq022.qlockin.entity.Attendance;
import com.decagosq022.qlockin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByCreatedByUserAndQlockInBetween(User user, LocalDateTime startOfDay, LocalDateTime endOfDay);

    Optional<Attendance> findByCreatedByUserAndQlockInBetween(User user, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Attendance> findAllByDate(LocalDate date);
}
