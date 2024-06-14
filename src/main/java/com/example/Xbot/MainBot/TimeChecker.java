package com.example.Xbot.MainBot;

import com.example.Xbot.MainBot.Models.Appointment;
import com.example.Xbot.MainBot.Models.Repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TimeChecker {

    private final AppointmentRepository appointmentRepository;


    @Autowired
    public TimeChecker(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }


    //@Scheduled(cron = "0 0 */2 * * *")
    @Scheduled(cron = "*/10 * * * * *")
    public void expired(){
        System.out.println("remove");
        List<Appointment> appointments = appointmentRepository.findAppointmentByAppointmentActivity(true);
        for (Appointment appointment : appointments) {
            Date expiredDate = new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L);

            if (appointment.getCreateDate().before(expiredDate)) {
                appointmentRepository.delete(appointment);
            }
        }
    }
}
