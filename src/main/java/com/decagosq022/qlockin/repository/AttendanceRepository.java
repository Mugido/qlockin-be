//package com.decagosq022.qlockin.repository;
//
//import com.decagosq022.qlockin.entity.Attendance;
//import com.decagosq022.qlockin.payload.response.AbsenteeismReportResponseDto;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface AttendanceRepository extends CrudRepository<Attendance, Long> {
//    @Query("SELECT new com.decagosq022.qlockin.payload.response.AbsenteeismReportResponseDto(u.id, u.fullName, " +
//            "SUM(CASE WHEN a.isAbsent = true THEN 1 ELSE 0 END), COUNT(a)) " +
//            "FROM User u LEFT JOIN Attendance a ON u.id = a.user.id " +
//            "GROUP BY u.id, u.fullName")
//    List<AbsenteeismReportResponseDto> findTotalAbsenteesPerUser();
//
//
//}
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
