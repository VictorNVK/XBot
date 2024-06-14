package com.example.Xbot.AdminBot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Getter
public class AdminBotConfig {
    @Value("${telegram.admin.bot.name}")
    private String BOT_NAME;

    @Value("${telegram.admin.bot.token}")
    private String BOT_TOKEN;
}
