package com.example.Xbot.MainBot.Models;

import com.example.Xbot.MainBot.Models.Enum.AppointmentType;
import com.example.Xbot.MainBot.Models.Enum.MyDayOfWeek;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated
    private MyDayOfWeek dayOfWeek;

    @Column
    private String bookingDate;

    @Column
    private String time;

    @Column
    private Integer table_number;

    @Column
    private String code;

    @Column
    private Long creator;

    @Column
    private Date createDate;

    @Column
    private AppointmentType appointmentType;

    @Column
    private Boolean appointmentActivity;



}
