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
