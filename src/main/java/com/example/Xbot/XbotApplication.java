package com.example.Xbot;

import com.example.Xbot.MainBot.Messager;
import com.example.Xbot.MainBot.Models.Repository.AppointmentRepository;
import com.example.Xbot.MainBot.Models.Repository.UserRepository;
import com.example.Xbot.MainBot.XBot;
import com.example.Xbot.MainBot.config.BotConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class XbotApplication {
    public static void main(String[] args) {
		SpringApplication.run(XbotApplication.class, args);
	}


}
