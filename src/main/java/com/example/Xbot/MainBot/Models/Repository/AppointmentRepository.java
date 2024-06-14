package com.example.Xbot.MainBot.Models.Repository;

import com.example.Xbot.MainBot.Models.Appointment;
import com.example.Xbot.MainBot.Models.Enum.AppointmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

     List<Appointment> findAppointmentsByAppointmentActivityAndCreator(Boolean Activity, Long creator);

     List<Appointment> findAppointmentByAppointmentActivity(Boolean activity);

     List<Appointment> findByAppointmentActivityAndAppointmentType(Boolean activity, AppointmentType appointmentType);

     Appointment findAppointmentByCode(String code);

     Appointment findAppointmentById(Integer id);
}
