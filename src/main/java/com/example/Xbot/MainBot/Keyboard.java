package com.example.Xbot.MainBot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;

import java.util.*;

@Component
public class Keyboard {

    public ReplyKeyboardMarkup starKb() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Бронирование➡\uFE0F");

        row1.add("Меню\uD83D\uDCDD");
        row1.add("Афиша\uD83E\uDEA7");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("Караоке\uD83C\uDFA4");
        row2.add("Задайте нам вопрос\uD83D\uDCAC");
        keyboardRows.add(row1);
        keyboardRows.add(row2);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    public InlineKeyboardMarkup menuKb() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Основное меню\uD83D\uDCDD");
        button1.setUrl("https://telegra.ph/Osnovnoe-menyu-06-05");
        row1.add(button1);
        rows.add(row1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Барное меню\uD83C\uDF7A");
        button2.setUrl("https://telegra.ph/Barnoe-menyu-06-04");
        row2.add(button2);
        rows.add(row2);

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Интерьер\uD83E\uDE91");
        button3.setUrl("https://telegra.ph/Interer-06-04");
        row3.add(button3);
        rows.add(row3);

        List<InlineKeyboardButton> row4 = new ArrayList<>();
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText("Назад❌");
        button4.setCallbackData("back");
        row4.add(button4);
        rows.add(row4);

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
    public InlineKeyboardMarkup daysOfWeekKb() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        Map<DayOfWeek, String> data = new HashMap<>();

        LocalDate currentDate = LocalDate.now();
        DayOfWeek todayDayOfWeek = currentDate.getDayOfWeek();
        data.put(todayDayOfWeek, currentDate.format(DateTimeFormatter.ofPattern("dd.MM")));

        for(int i = 1; i < 7; i++){
            LocalDate day = currentDate.plusDays(i);
            data.put(day.getDayOfWeek(), day.format(DateTimeFormatter.ofPattern("dd.MM")));
        }
        List<InlineKeyboardButton> row = new ArrayList<>();
        for(int i = 1; i <= 7; i++){

             String date = data.get(DayOfWeek.of(i));
             String dayOfWeek = DayOfWeek.of(i).getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ru"));
                String abbreviatedDayOfWeek = switch (dayOfWeek) {
                case "воскресенье" -> "вс";
                case "понедельник" -> "пн";
                case "вторник" -> "вт";
                case "среда" -> "ср";
                case "четверг" -> "чт";
                case "пятница" -> "пт";
                case "суббота" -> "сб";
                default -> "";
            };


            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(abbreviatedDayOfWeek + " " + date);
            button.setCallbackData("day" + dayOfWeek.toUpperCase() + " " + date);
            row.add(button);
            if(row.size() == 3 || i == 7){
                rows.add(row);
                row = new ArrayList<>();
            }
        }

        List<InlineKeyboardButton> lastRow = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("Назад❌");
        backButton.setCallbackData("back");
        lastRow.add(backButton);

        InlineKeyboardButton otherDateButton = new InlineKeyboardButton();
        otherDateButton.setText("Другая дата\uD83D\uDCC6");
        otherDateButton.setCallbackData("other_date");
        lastRow.add(otherDateButton);

        rows.add(lastRow);

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup bookTable2Kb(Resources resources) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 0; i < resources.getTimes().size(); i += 3) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            for (int j = i; j < i + 3 && j < resources.getTimes().size(); j++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(resources.getTimes().get(j));
                button.setCallbackData("t" + resources.getTimes().get(j));
                row.add(button);
            }

            rows.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public ReplyKeyboardMarkup bookTableChoiceKb() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row0 = new KeyboardRow();

        row0.add("Стол\uD83C\uDF74");
        row0.add("Бар\uD83C\uDF7A");


        KeyboardRow row1 = new KeyboardRow();
        row1.add("Назад❌");
        keyboardRows.add(row0);
        keyboardRows.add(row1);

        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    public InlineKeyboardMarkup bookTable3Kb(Resources resources) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 0; i < resources.getTables().size(); i += 3) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            for (int j = i; j < i + 3 && j < resources.getTables().size(); j++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(String.valueOf(resources.getTables().get(j)));
                button.setCallbackData("tb" + resources.getTables().get(j));
                row.add(button);
            }

            rows.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public ReplyKeyboardMarkup bookTableFinalKb() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row0 = new KeyboardRow();

        row0.add("Подтвердить✅");

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Назад❌");
        keyboardRows.add(row0);
        keyboardRows.add(row1);

        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    public ReplyKeyboardMarkup makeQuestionKb() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Назад❌");
        keyboardRows.add(row1);

        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    public ReplyKeyboardMarkup bookingAfterPosterKb(String chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add("Бронирование➡\uFE0F");
        row2.add("Назад❌");
        keyboardRows.add(row1);
        keyboardRows.add(row2);

        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

}
