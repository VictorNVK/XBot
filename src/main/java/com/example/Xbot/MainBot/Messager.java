package com.example.Xbot.MainBot;

import com.example.Xbot.MainBot.Image.ImageGenerator;
import com.example.Xbot.MainBot.Models.Appointment;
import com.example.Xbot.MainBot.Models.Image;
import com.example.Xbot.MainBot.Models.Repository.AppointmentRepository;
import com.example.Xbot.MainBot.config.BotConfig;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class Messager {

    private final AppointmentRepository appointmentRepository;
    private final Resources resources;
    private final BotConfig botConfig;
    private final Keyboard keyboard;

    @Autowired
    public Messager(AppointmentRepository appointmentRepository, Resources resources, BotConfig botConfig, Keyboard keyboard) {
        this.appointmentRepository = appointmentRepository;
        this.resources = resources;
        this.botConfig = botConfig;
        this.keyboard = keyboard;
    }

    public SendMessage start(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Доброго времени суток, вас приветствует бот XClub Я ваш помощник по всем " +
                "интересующим" +
                " " +
                "вас вопросам:\n" + " включая <b>бесплатный вход</b>, бронирование столов, и тд.\n\n Чем я могу " +
                "помочь?");

        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setReplyMarkup(keyboard.starKb());
        return sendMessage;
    }

    public SendMessage otherStart(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Чем я могу помочь?");

        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setReplyMarkup(keyboard.starKb());
        return sendMessage;
    }


    public SendMessage menu(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("У нас имеются основное и барное меню, что вас интересует:");
        sendMessage.setReplyMarkup(keyboard.menuKb());
        return sendMessage;

    }

    public SendPhoto poster(String url, String chatId) {
        SendPhoto sendMessage = new SendPhoto();
        sendMessage.setChatId(chatId);
        sendMessage.setPhoto(new InputFile(url));

        return sendMessage;
    }

    public SendMessage makeQuestion(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Напишите, что именно вас интересует? Или может столкнулись с проблемой? Опишите её, я вам помогу");
        sendMessage.setReplyMarkup(keyboard.makeQuestionKb());
        return sendMessage;
    }

    public SendMessage booking(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Выберите что хотите забронировать");
        sendMessage.setChatId(chatId);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Караоке\uD83C\uDFA4");
        row1.add("Столик\uD83C\uDF74");
        keyboardRows.add(row1);
        KeyboardRow row2 = new KeyboardRow();
        row2.add("Как получить клубную карту\uD83C\uDF81");
        keyboardRows.add(row2);

        keyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(keyboardMarkup);

        return sendMessage;
    }

    public SendMessage bookTable1(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Отлично, давайте выберем день, когда вы придете");
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(keyboard.daysOfWeekKb());
        return sendMessage;
    }

    public SendMessage bookTable2(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Выберите время :");
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(keyboard.bookTable2Kb(resources));
        return sendMessage;
    }

    public SendMessage bookTableChoice(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Вам нужен столик или бар?");
        sendMessage.setReplyMarkup(keyboard.bookTableChoiceKb());
        return sendMessage;

    }

    public SendPhoto bookTable3(String chatId) {
        SendPhoto sendMessage = new SendPhoto();
        sendMessage.setCaption("Выберите номер столика:");
        sendMessage.setChatId(chatId);
        sendMessage.setPhoto(botConfig.getTables());
        sendMessage.setReplyMarkup(keyboard.bookTable3Kb(resources));
        return sendMessage;
    }

    public SendMessage bookTableFinal(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Вы уверены что хотите создать бронь?");
        sendMessage.setReplyMarkup(keyboard.bookTableFinalKb());
        return sendMessage;
    }

    @Transactional
    public SendPhoto getCode(String chatId, Appointment appointment) throws IOException {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        ImageGenerator imageGenerated = new ImageGenerator();
        sendPhoto.setPhoto(new InputFile(new File("MyImages/" + imageGenerated.create())));

        String code = imageGenerated.getCode();
        appointment.setCode(code);
        appointmentRepository.save(appointment);

        return sendPhoto;
    }

    public SendMessage sendAppointment(String chatId, Appointment appointment) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Ваша бронь:\n" +
                "День недели " + appointment.getDayOfWeek() +
                "\nДата: " + appointment.getBookingDate() +
                "\nВремя " + appointment.getTime() +
                "\nСтолик " + appointment.getTable_number());
        return sendMessage;
    }

    public SendMessage end(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Мы рады, что вы выбрали наш клуб для проведения вашего вечера\n\n" +
                "Ваш столик будет зарезервирован. Ждем вас с нетерпением, гарантируем, что вас ждет незабываемое " +
                "времяпровождение!\n\n" +
                "Вход бесплатный по промокоду на изображении\uD83C\uDF81");
        return sendMessage;
    }

    public SendMessage getCard(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Клубную карту можно получить после пяти посещений нашего клуба!");
        return sendMessage;
    }

    public SendMessage getBarAppointment(String chatId, Appointment appointment) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Ваша бронь:\n" +
                "День недели: " + appointment.getDayOfWeek() +
                "\nДата: " + appointment.getBookingDate() +
                "\nВремя : " + appointment.getTime() +
                "\nМесто : " + "Бар");
        return sendMessage;
    }

    public SendMessage getKaraokeAppointment(String chatId, Appointment appointment) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Ваша бронь:\n" +
                "День недели: " + appointment.getDayOfWeek() +
                "\nДата: " + appointment.getBookingDate() +
                "\nВремя: " + appointment.getTime() +
                "\nМесто: " + "Караоке");
        return sendMessage;
    }

    public SendMessage posterNotFound(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Сейчас для этого дня нет афиши, попробуйте позже!");
        return sendMessage;
    }

    public SendPhoto interior(String chatId) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(botConfig.getTables());
        return sendPhoto;
    }

    public SendMessage bookingAfterPoster(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выберите пункт:");
        sendMessage.setReplyMarkup(keyboard.bookingAfterPosterKb(chatId));
        return sendMessage;
    }

    public SendMessage other_date(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Введите дату в формте дд.мм, например 04.06");
        sendMessage.setChatId(chatId);
        return sendMessage;
    }
}
