package com.example.Xbot;

import com.example.Xbot.AdminBot.AdminMessager;
import com.example.Xbot.AdminBot.AdminXbot;
import com.example.Xbot.AdminBot.config.AdminBotConfig;
import com.example.Xbot.AdminBot.model.repository.AdminRepository;
import com.example.Xbot.MainBot.Image.ImageGenerator;
import com.example.Xbot.MainBot.Messager;
import com.example.Xbot.MainBot.Models.Repository.AppointmentRepository;
import com.example.Xbot.MainBot.Models.Repository.ImageRepository;
import com.example.Xbot.MainBot.Models.Repository.QuestionRepository;
import com.example.Xbot.MainBot.Models.Repository.UserRepository;
import com.example.Xbot.MainBot.XBot;
import com.example.Xbot.MainBot.config.BotConfig;
import lombok.RequiredArgsConstructor;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@RequiredArgsConstructor
public class Initializer {
    private final BotConfig botConfig;
    private final Messager messager;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final AdminBotConfig adminBotConfig;
    private final AdminRepository adminRepository;
    private final AdminMessager adminMessager;
    private final QuestionRepository questionRepository;
    private final ImageRepository imageRepository;


    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            botsApi.registerBot(new XBot(botConfig, messager, userRepository, appointmentRepository,
                    questionRepository, imageRepository));
            botsApi.registerBot(new AdminXbot(adminBotConfig, adminRepository, adminMessager, appointmentRepository,
                    questionRepository, imageRepository));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
