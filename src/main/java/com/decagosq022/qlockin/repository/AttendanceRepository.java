
package com.decagosq022.qlockin.repository;

import com.decagosq022.qlockin.entity.Attendance;
import com.decagosq022.qlockin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByCreatedByUserAndQlockInBetween(User user, LocalDateTime startOfDay, LocalDateTime endOfDay);

    Optional<Attendance> findByCreatedByUserAndQlockInBetween(User user, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Attendance> findAllByDate(LocalDate date);

    List<Attendance> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Attendance> findByCreatedByUserAndDateBetween(User user, LocalDate weekStart, LocalDate weekEnd);

    @Query("SELECT a FROM Attendance a WHERE a.createdByUser.id = :userId AND YEAR(a.date) = :year AND MONTH(a.date) = :month")
    List<Attendance> findByCreatedByUserAndYearAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    List<Attendance> findByCreatedByUser(User user);

    Optional<Attendance> findByCreatedByUserAndDate(User employee, LocalDate today);


    @Query("SELECT a FROM Attendance a WHERE a.createdByUser.email = :email AND YEAR(a.date) = :year AND MONTH(a.date) = :month")
    List<Attendance> findAttendanceByCreatedByUserEmailAndMonth(
            @Param("email") String email,
            @Param("year") int year,
            @Param("month") int month);

    List<Attendance> findAttendanceByCreatedByUserEmailAndDateBetween(String email, LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM Attendance a WHERE a.createdByUser.id = :userId AND YEAR(a.date) = :year AND MONTH(a.date) = :month")
    List<Attendance> findAttendanceByCreatedByUserAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

}
