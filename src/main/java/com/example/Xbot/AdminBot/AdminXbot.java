package com.example.Xbot.AdminBot;

import com.example.Xbot.AdminBot.config.AdminBotConfig;

import com.example.Xbot.AdminBot.model.adminEnum.AdminState;
import com.example.Xbot.AdminBot.model.entity.Admin;
import com.example.Xbot.AdminBot.model.repository.AdminRepository;
import com.example.Xbot.MainBot.Models.Appointment;
import com.example.Xbot.MainBot.Models.Enum.AppointmentType;
import com.example.Xbot.MainBot.Models.Image;
import com.example.Xbot.MainBot.Models.Quest;
import com.example.Xbot.MainBot.Models.Repository.AppointmentRepository;
import com.example.Xbot.MainBot.Models.Repository.ImageRepository;
import com.example.Xbot.MainBot.Models.Repository.QuestionRepository;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


import java.util.*;

public class AdminXbot extends TelegramLongPollingBot {

    private final AdminBotConfig adminBotConfig;
    private final AdminRepository adminRepository;
    private AdminMessager adminMessager;
    private final AppointmentRepository appointmentRepository;
    private final QuestionRepository questionRepository;
    private final ImageRepository imageRepository;

    public AdminXbot(AdminBotConfig adminBotConfig, AdminRepository adminRepository, AdminMessager adminMessager, AppointmentRepository appointmentRepository, QuestionRepository questionRepository, ImageRepository imageRepository) {
        this.adminBotConfig = adminBotConfig;
        this.adminRepository = adminRepository;
        this.adminMessager = adminMessager;
        this.appointmentRepository = appointmentRepository;
        this.questionRepository = questionRepository;
        this.imageRepository = imageRepository;
    }

    private List<Admin> admins = new ArrayList<>();
    private Map<Long, Admin> adminMap = new HashMap<>();
    private HashMap<String, Image> images = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {

        Admin admin = null;
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (adminRepository.findAll().isEmpty() || adminRepository.existsAdminByUsername(update.getMessage().getFrom().getUserName())) {
                if (adminMap.containsKey(update.getMessage().getChatId())) {
                    admin = adminMap.get(update.getMessage().getChatId());
                } else {
                    admin = adminRepository.findAdminById(update.getMessage().getFrom().getId());
                    if (admin == null) {
                        admin = adminRepository.findAdminByUsername(update.getMessage().getFrom().getUserName());
                        if (admin == null) {
                            admin = new Admin();
                            admin.setId(update.getMessage().getFrom().getId());
                            admin.setUsername(update.getMessage().getFrom().getUserName());
                            adminRepository.save(admin);
                        } else {
                            admin.setId(update.getMessage().getFrom().getId());
                            adminRepository.save(admin);
                        }
                    }
                    admins.add(admin);
                    adminMap.put(update.getMessage().getChatId(), admin);
                }
                commandHandler(update, admin);
            }

             } else if (update.hasCallbackQuery()) {
                if (adminRepository.existsAdminByUsername(update.getCallbackQuery().getFrom().getUserName())) {
                    admin = adminRepository.findAdminById(update.getCallbackQuery().getFrom().getId());
                    callbackHandler(update, admin);
                }
            }
        }


    @SneakyThrows
    private void commandHandler(Update update, Admin admin) {
        String chatId = update.getMessage().getChat().getId().toString();
        String username = update.getMessage().getFrom().getUserName();
        String command = update.getMessage().getText();
        if (command.startsWith("/start")) {
            admin.setAdminState(AdminState.START);
            adminRepository.save(admin);
            execute(adminMessager.start(chatId));
        }
        if (command.startsWith("/menu")) {
            execute(adminMessager.menu(chatId));
        }
        if (command.startsWith("/orders")) {
            admin.setAdminState(AdminState.SEARCH);
            adminRepository.save(admin);
            execute(adminMessager.choiceOrdersType(chatId));
        }
        if (command.startsWith("/admins")) {
            admin.setAdminState(AdminState.ADMINS);
            adminRepository.save(admin);
            execute(adminMessager.admins(chatId));
            return;
        }
        if (command.startsWith("/support")) {
            admin.setAdminState(AdminState.SUPPORT);
            adminRepository.save(admin);
            List<Quest> quests = questionRepository.findAll();
            for (Quest quest : quests) {
                execute(adminMessager.getQuestion(chatId, quest));
            }
            if (quests.isEmpty()) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Вопросов пока-что нет!");
                execute(sendMessage);
            }
            execute(adminMessager.menu(chatId));
            admin.setAdminState(AdminState.START);
            adminRepository.save(admin);
        }
        if (command.startsWith("/images")) {
            admin.setAdminState(AdminState.IMAGES);
            adminRepository.save(admin);
            Image image = new Image();
            imageRepository.save(image);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Введите url для новой афиши");
            execute(sendMessage);
            images.put(chatId, image);
            return;
        }

        if (admin.getAdminState().equals(AdminState.SEARCH)) {
            if (command.startsWith("Столики")) {
                List<Appointment> appointments = appointmentRepository.findByAppointmentActivityAndAppointmentType(true,
                        AppointmentType.TABLE);
                for (Appointment appointment : appointments) {
                    execute(adminMessager.sendAppointment(chatId, appointment));
                }
                execute(adminMessager.choiceOrdersType(chatId));
            }
            if (command.startsWith("Бар")) {
                List<Appointment> appointments = appointmentRepository.findByAppointmentActivityAndAppointmentType(true, AppointmentType.BAR);
                for (Appointment appointment : appointments) {
                    execute(adminMessager.sendAppointment(chatId, appointment));
                }
                execute(adminMessager.choiceOrdersType(chatId));
            }

            if (command.startsWith("Караоке")) {
                List<Appointment> appointments = appointmentRepository.findByAppointmentActivityAndAppointmentType(true, AppointmentType.KARAOKE);
                for (Appointment appointment : appointments) {
                    execute(adminMessager.sendAppointment(chatId, appointment));
                }
                execute(adminMessager.choiceOrdersType(chatId));
            }
            if(command.startsWith("Все")){
                List<Appointment> appointments = appointmentRepository.findAll();
                for (Appointment appointment : appointments) {
                    execute(adminMessager.sendAppointment(chatId, appointment));
                }
                execute(adminMessager.choiceOrdersType(chatId));
            }

            if (command.startsWith("По коду")) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                admin.setAdminState(AdminState.CODE);
                adminRepository.save(admin);
                sendMessage.setText("Введите код:");
                execute(sendMessage);
                return;
            }
        }
        if (admin.getAdminState().equals(AdminState.ADMINS)) {
            if (command.startsWith("Добавить")) {
                admin.setAdminState(AdminState.ADD_ADMIN);
                adminRepository.save(admin);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Введите имя нового админа(без @ в начале)");
                execute(sendMessage);
                return;
            }
            if (command.startsWith("Удалить")) {
                admin.setAdminState(AdminState.REMOVE_ADMIN);
                adminRepository.save(admin);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Введите имя админа для удаления(без @ в начале)");
                execute(sendMessage);
                return;
            }
            if (command.startsWith("Список админов")) {
                List<Admin> allModerators = adminRepository.findAll();
                List<String> moderatorsNames = new ArrayList<>();
                for (Admin admin1 : allModerators) {
                    moderatorsNames.add(admin1.getUsername());
                }
                execute(adminMessager.moderList(moderatorsNames, chatId));
                admin.setAdminState(AdminState.START);
                adminRepository.save(admin);
                execute(adminMessager.menu(chatId));

            }
        }
        if (admin.getAdminState().equals(AdminState.ADD_ADMIN)) {
            String newAdminName = command;
            Admin newAdmin = new Admin();
            newAdmin.setUsername(newAdminName);
            adminRepository.save(newAdmin);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Новый админ с именем " + command + " был добавлен");
            execute(sendMessage);
            sendMessage.setChatId(chatId);
            admin.setAdminState(AdminState.START);
            adminRepository.save(admin);
            execute(adminMessager.menu(chatId));
        }
        if (admin.getAdminState().equals(AdminState.REMOVE_ADMIN)) {
            Admin delAdmin = adminRepository.findAdminByUsername(command);
            adminRepository.delete(delAdmin);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Админ с именем " + command + " был успешно удалён");
            execute(sendMessage);
        }

        if (admin.getAdminState().equals(AdminState.CODE)) {
            String code = command;
            Appointment appointment = appointmentRepository.findAppointmentByCode(code);
            if (appointment != null) {
                execute(adminMessager.findAppointmentByCode(chatId, appointment));
            } else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("❌Бронь с таким кодом не найдена❌");
                execute(sendMessage);
            }

            admin.setAdminState(AdminState.SEARCH);
            adminRepository.save(admin);
            execute(adminMessager.choiceOrdersType(chatId));
        }
        if (admin.getAdminState().equals(AdminState.IMAGES)) {
            String url = command;
            Image image = images.get(admin.getId().toString());
            image.setUrl(url);
            imageRepository.save(image);
            images.remove(admin.getId().toString());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Афиша была успешно обновлена");

            execute(sendMessage);
            execute(adminMessager.menu(chatId));
            admin.setAdminState(AdminState.START);
            adminRepository.save(admin);
        }
        if (command.startsWith("Назад")) {
            if (admin.getAdminState().equals(AdminState.ADMINS)) {
                admin.setAdminState(AdminState.START);
                adminRepository.save(admin);
                execute(adminMessager.menu(chatId));
            }
            if (admin.getAdminState().equals(AdminState.IMAGES)) {
                admin.setAdminState(AdminState.START);
                adminRepository.save(admin);
                execute(adminMessager.menu(chatId));
            }
        }
    }

    @SneakyThrows
    private void callbackHandler(Update update, Admin admin) {
        String callback = update.getCallbackQuery().getData();
        String chatId = String.valueOf(update.getCallbackQuery().getFrom().getId());
        if (callback.startsWith("Close")) {
            Integer questId = Integer.parseInt(callback.substring(5));
            Quest quest = questionRepository.findQuestById(questId);
            questionRepository.delete(quest);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Вопрос под номером " + questId + " был удалён!");
            execute(sendMessage);
        }
        if(callback.startsWith("del")){
            String appointmentId = callback.substring(3);
            Integer id = Integer.valueOf(appointmentId);
            Appointment appointment = appointmentRepository.findAppointmentById(id);
            appointmentRepository.delete(appointment);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Бронь была успешна удалена!");
            sendMessage.setChatId(chatId);
            execute(sendMessage);
        }
    }

    @Override
    public String getBotUsername() {
        return adminBotConfig.getBOT_NAME();
    }

    @Override
    public String getBotToken() {
        return adminBotConfig.getBOT_TOKEN();
    }

}
