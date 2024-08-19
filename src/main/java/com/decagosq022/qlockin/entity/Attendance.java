package com.decagosq022.qlockin.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "attendance_tbl")
public class Attendance extends BaseEntity{

    private LocalDateTime default_Qlock_In;

    private LocalDateTime default_Qlock_Out;

    @ManyToOne(optional = true)
    @JoinColumn(name = "qlockIn_user_id", foreignKey = @ForeignKey(name = "FK_attendance_created_by_user"))
    @JsonBackReference("createdByUser")
    private User createdByUser;

}
