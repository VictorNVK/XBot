package com.example.Xbot.AdminBot;

import com.example.Xbot.MainBot.Models.Appointment;
import com.example.Xbot.MainBot.Models.Quest;
import com.example.Xbot.MainBot.Models.Repository.UserRepository;
import com.example.Xbot.MainBot.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdminMessager {

    private final UserRepository userRepository;

    @Autowired
    public AdminMessager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public SendMessage start(String chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Здравствуйте администратор. Добро пожаловать в админ панель Xbot!\n\n"+
        "Здесь вы сможете управлять ботом Xbot\n\n" +
                "Используйте /menu что бы узнать весь функционал админ панели");
        return sendMessage;
    }

    public SendMessage menu(String chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Список функций для управления ботом\uD83D\uDCDD\n\n"+
        "/orders - Получить все текущие бронирования\n" + "/images - установить афиши\n" + "/support - Поддержка " +
                "клиентов"
        + "\n/admins - Добавить админа");
        return sendMessage;
    }
    public SendMessage choiceOrdersType(String chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выберите тип поиска");


        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Столики\uD83E\uDD44");
        row1.add("Бар\uD83C\uDF7B");
        row1.add("Караоке\uD83C\uDFA4");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("По коду\uD83E\uDDD1\u200D\uD83D\uDCBB");
        row2.add("Все");
        keyboardRows.add(row1);
        keyboardRows.add(row2);

        keyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(keyboardMarkup);

        return sendMessage;
    }
    public SendMessage sendAppointment(String chatId, Appointment appointment){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        User user = userRepository.findById(appointment.getCreator());

        sendMessage.setText("День недели: " + appointment.getDayOfWeek() +
                "\nДата: " + appointment.getBookingDate() +
                "\nТип: " + appointment.getAppointmentType() +
        "\nВремя: " + appointment.getTime() +
                "\nНомер столика: " + appointment.getTable_number() +
                "\nСоздатель: " + user.getUsername()+
                "\nКОД: " + appointment.getCode());
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton remove = new InlineKeyboardButton();
        remove.setText("Удалить❌");
        remove.setCallbackData("del" + appointment.getId());
        row.add(remove);
        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return sendMessage;
    }
    public SendMessage findAppointmentByCode(String chatId, Appointment appointment){
        SendMessage sendMessage = new SendMessage();
        User user = userRepository.findById(appointment.getCreator());
        sendMessage.setChatId(chatId);
        sendMessage.setText("День недели: " + appointment.getDayOfWeek() +
                "\nТип :" + appointment.getAppointmentType() +
                "\nДата: " + appointment.getBookingDate() +
                "\nВремя: " + appointment.getTime() +
                "\nНомер столика: " + appointment.getTable_number() +
                "\nСоздатель: " + user.getUsername()+
                "\nКОД: " + appointment.getCode());

        return sendMessage;
    }

    public SendMessage admins(String chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выберите пункт");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add("Добавить\uD83D\uDFE2");
        row1.add("Удалить\uD83D\uDED1");
        row2.add("Назад❌");
        row2.add("Список админов\uD83E\uDDFE");
        keyboardRows.add(row1);
        keyboardRows.add(row2);

        keyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(keyboardMarkup);

        return sendMessage;
    }

    public SendMessage getQuestion(String chatId, Quest quest){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Номер вопроса" +quest.getId() +
                "Вопрос от: " + "@" + quest.getOwnerName()  +
                "\nВопрос :\n" +  quest.getQuest());

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton remove = new InlineKeyboardButton();
        remove.setText("Закрыть❌");
        remove.setCallbackData("Close" + quest.getId());
        row.add(remove);
        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }



    public SendMessage moderList(List<String> moderators, String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        StringBuilder messageBuilder = new StringBuilder();
        for (String moderator : moderators) {
            messageBuilder.append("@").append(moderator).append("\n");
        }
        sendMessage.setText("Вот список всех существующих админов\n" + messageBuilder.toString());

        return sendMessage;
    }

}
