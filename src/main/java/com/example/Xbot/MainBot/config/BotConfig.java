package com.example.Xbot.MainBot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Configuration
@Getter
@Setter
public class BotConfig {

    @Value("${telegram.bot.name}")
    private String BOT_NAME;

    @Value("${telegram.bot.token}")
    private String BOT_TOKEN;

    ClassLoader classLoader = getClass().getClassLoader();

    public InputFile getTables() {
        try (InputStream inputStream = classLoader.getResourceAsStream("image/menu/tables.jpeg")) {
            File tempFile = File.createTempFile("temp-image-", ".jpg");
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            InputFile inputFile = new InputFile(tempFile);
            tempFile.deleteOnExit();
            return inputFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public InputFile getBarMenu() {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("image/menu/bar_menu.pdf")) {
            File tempFile = File.createTempFile("bar-menu", ".pdf");
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            InputFile inputFile = new InputFile(tempFile);
            tempFile.deleteOnExit();
            return inputFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
