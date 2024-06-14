package com.example.Xbot.MainBot;

import com.example.Xbot.MainBot.Image.ImageGenerator;
import com.example.Xbot.MainBot.Models.Appointment;
import com.example.Xbot.MainBot.Models.Enum.AppointmentType;
import com.example.Xbot.MainBot.Models.Enum.MyDayOfWeek;
import com.example.Xbot.MainBot.Models.Image;
import com.example.Xbot.MainBot.Models.Quest;
import com.example.Xbot.MainBot.Models.Repository.AppointmentRepository;
import com.example.Xbot.MainBot.Models.Repository.ImageRepository;
import com.example.Xbot.MainBot.Models.Repository.QuestionRepository;
import com.example.Xbot.MainBot.Models.Repository.UserRepository;
import com.example.Xbot.MainBot.Models.User;
import com.example.Xbot.MainBot.Models.Enum.UserState;
import com.example.Xbot.MainBot.config.BotConfig;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Component
public class XBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    private final Messager messager;

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final QuestionRepository questionRepository;
    private final ImageRepository imageRepository;


    private List<User> users = new ArrayList<>();
    private Map<Long, User> userMap = new HashMap<>();

    public XBot(BotConfig botConfig, Messager messager, UserRepository userRepository, AppointmentRepository appointmentRepository, QuestionRepository questionRepository, ImageRepository imageRepository) {
        this.botConfig = botConfig;
        this.messager = messager;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.questionRepository = questionRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            User user;
            if (userMap.containsKey(update.getMessage().getChatId())) {
                user = userMap.get(update.getMessage().getChatId());
            } else {
                user = userRepository.findById(update.getMessage().getFrom().getId());
                if (user == null) {
                    user = new User();
                    user.setId(update.getMessage().getFrom().getId());
                    user.setUsername(update.getMessage().getFrom().getUserName());
                    userRepository.save(user);
                }
                users.add(user);
                userMap.put(update.getMessage().getChatId(), user);
            }
            commandHandler(update, user);
        } else if (update.hasCallbackQuery()) {
            callbackHandler(update);
        }
    }

    @SneakyThrows
    private void commandHandler(Update update, User user) {
        String chatId = update.getMessage().getChat().getId().toString();
        String username = update.getMessage().getFrom().getUserName();
        String command = update.getMessage().getText();
        if (command.startsWith("/start") && user.getUserState() == null) {
            clearAppointments(appointmentRepository.findAppointmentsByAppointmentActivityAndCreator(false, Long.valueOf(chatId)));

            user.setUserState(UserState.START);
            userRepository.save(user);
            execute(messager.start(chatId));
        }
        if (command.startsWith("Меню")) {
            user.setUserState(UserState.MENU);
            userRepository.save(user);
            execute(messager.menu(chatId));
        }
        if (command.startsWith("Задайте нам вопрос")) {
            user.setUserState(UserState.QUESTION);
            userRepository.save(user);
            execute(messager.makeQuestion(chatId));
            return;
        }
        if (command.startsWith("Бронирование")) {
            user.setUserState(UserState.BOOKING);
            userRepository.save(user);
            execute(messager.booking(chatId));
        }
        if (command.startsWith("Афиша") && user.getUserState().equals(UserState.START) || user.getUserState().equals(UserState.POSTER)) {
            user.setUserState(UserState.BOOKING);
            userRepository.save(user);
            List<Image> images = imageRepository.findAll();
            if (images.isEmpty()) {
                execute(messager.posterNotFound(chatId));
            }

            for (Image image : images) {
                try {
                    execute(messager.poster(image.getUrl(), chatId));
                } catch (Exception e) {
                    Image image1 = imageRepository.findImageById(image.getId());
                    imageRepository.delete(image1);
                }
            }
            execute(messager.bookingAfterPoster(chatId));
            return;

        }
        if (command.startsWith("Как получить клубную карту") && user.getUserState().equals(UserState.BOOKING)) {
            execute(messager.getCard(chatId));
            execute(messager.booking(chatId));
        }
        if (command.startsWith("Караоке")) {
            user.setUserState(UserState.BOOKING);
            userRepository.save(user);
            Appointment appointment = new Appointment();
            appointment.setCreator(Long.valueOf(chatId));
            appointment.setAppointmentActivity(false);
            appointment.setAppointmentType(AppointmentType.KARAOKE);
            appointmentRepository.save(appointment);
            execute(messager.bookTable1(chatId));
        }
        if (command.startsWith("Столик") & user.getUserState().equals(UserState.BOOKING)) {
            Appointment appointment = new Appointment();
            appointment.setCreator(Long.valueOf(chatId));
            appointment.setAppointmentActivity(false);
            appointment.setAppointmentType(AppointmentType.TABLE);
            appointmentRepository.save(appointment);
            execute(messager.bookTable1(chatId));

        }
        if (command.startsWith("Стол\uD83C\uDF74") & user.getUserState().equals(UserState.BOOKING)) {
            execute(messager.bookTable3(chatId));
            user.setUserState(UserState.BOOKING_TABLE);
            userRepository.save(user);

        }
        if (command.startsWith("Бар") & user.getUserState().equals(UserState.BOOKING)) {
            Appointment appointment = clearAppointments(appointmentRepository.findAppointmentsByAppointmentActivityAndCreator(false, Long.valueOf(chatId)));
            appointment.setAppointmentType(AppointmentType.BAR);
            appointment.setCreateDate(new Date());
            appointmentRepository.save(appointment);
            user.setUserState(UserState.FINAL);
            userRepository.save(user);
            execute(messager.getBarAppointment(chatId, appointment));
            execute(messager.bookTableFinal(chatId));

        }
        if (command.startsWith("Подтвердить")) {
            user = userRepository.findById(Long.valueOf(chatId));
            if (user.getUserState().equals(UserState.FINAL)) {
                Appointment appointment = clearAppointments(appointmentRepository.findAppointmentsByAppointmentActivityAndCreator(false, Long.valueOf(chatId)));
                execute(messager.getCode(chatId, appointment));
                ImageGenerator imageGenerator = new ImageGenerator();
                imageGenerator.deleteFile("code_" + appointment.getCode() + ".png");

                appointment.setAppointmentActivity(true);
                appointment.setCreateDate(new Date());
                appointmentRepository.save(appointment);
                execute(messager.end(chatId));
                user.setUserState(UserState.START);
                userRepository.save(user);
                execute(messager.otherStart(chatId));
                expired();
            }
        }

        if (command.startsWith("Назад")) {
            if (user.getUserState().equals(UserState.MENU)) {
                user.setUserState(UserState.START);
                userRepository.save(user);
                execute(messager.otherStart(chatId));
            }
            if (user.getUserState().equals(UserState.QUESTION)) {
                user.setUserState(UserState.START);
                userRepository.save(user);
                execute(messager.otherStart(chatId));
            }
            if (user.getUserState().equals(UserState.BOOKING) || user.getUserState().equals(UserState.BOOKING_TABLE) || user.getUserState().equals(UserState.FINAL)) {
                List<Appointment> appointments = appointmentRepository.findAppointmentsByAppointmentActivityAndCreator(false, Long.valueOf(chatId));
                if (!appointments.isEmpty()) {
                    appointmentRepository.delete(clearAppointments(appointments));
                }
                user.setUserState(UserState.START);
                userRepository.save(user);
                execute(messager.otherStart(chatId));
            }
        }
        if (user.getUserState().equals(UserState.QUESTION)) {
            String question = command;
            Quest quest = new Quest();
            User owner = userRepository.findById(Long.valueOf(chatId));
            quest.setOwner(owner);
            quest.setQuest(question);
            quest.setOwnerName(username);
            questionRepository.save(quest);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Ваш вопрос был отправлен! Ожидайте когда с вами свяжется поддержка");
            execute(sendMessage);
            user.setUserState(UserState.START);
            userRepository.save(user);
            execute(messager.otherStart(chatId));
        }
        if (user.getUserState().equals(UserState.OTHER_DATE)) {
            String date = command;
            String pattern = "^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012])$";

            if (date.matches(pattern)) {
                LocalDate currentDate = LocalDate.now();

                Appointment appointment =
                        clearAppointments(appointmentRepository.findAppointmentsByAppointmentActivityAndCreator(false, Long.valueOf(chatId)));
                appointment.setBookingDate(date);
                appointment.setCreator(Long.valueOf(chatId));
                String[] parts = date.split("\\.");
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);

                int year = LocalDate.now().getYear();
                LocalDate localDate = LocalDate.of(year, month, day);
                if (localDate.isAfter(currentDate) || localDate.isEqual(currentDate)) {
                    DayOfWeek dayOfWeek = localDate.getDayOfWeek();
                    String dayOfWeekRu = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ru"));
                    appointment.setDayOfWeek(MyDayOfWeek.valueOf(dayOfWeekRu.toUpperCase()));

                    appointmentRepository.save(appointment);
                    execute(messager.bookTable2(chatId));
                    user.setUserState(UserState.BOOKING_TIME);

                    userRepository.save(user);
                } else {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Ваша дата уже в прошлом!");
                    execute(sendMessage);
                    user.setUserState(UserState.START);
                    userRepository.save(user);
                    execute(messager.otherStart(chatId));
                }
            } else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Формат вашей даты не правильный!");
                execute(sendMessage);
                user.setUserState(UserState.START);
                userRepository.save(user);
                execute(messager.otherStart(chatId));
            }
        }
    }

    @SneakyThrows
    private void callbackHandler(Update update) {
        String callback = update.getCallbackQuery().getData();
        String id = String.valueOf(update.getCallbackQuery().getFrom().getId());
        long idLong = update.getCallbackQuery().getFrom().getId();
        User user = userRepository.findById(idLong);
        if (callback.startsWith("day") && user.getUserState().equals(UserState.BOOKING)) {
            callback = callback.substring(3);
            String[] parts = callback.split(" ");
            String dayOfWeek = parts[0];
            String date = parts[1];
            Appointment appointment = clearAppointments(appointmentRepository.findAppointmentsByAppointmentActivityAndCreator(false, idLong));
            appointment.setCreator(update.getCallbackQuery().getMessage().getChatId());
            appointment.setDayOfWeek(MyDayOfWeek.valueOf(dayOfWeek));
            appointment.setBookingDate(date);
            appointmentRepository.save(appointment);
            execute(messager.bookTable2(id));
            user.setUserState(UserState.BOOKING_TIME);
            userRepository.save(user);
        }
        if (callback.startsWith("t") && user.getUserState().equals(UserState.BOOKING_TIME)) {
            String time = callback.substring(1);
            Appointment appointment = clearAppointments(appointmentRepository.findAppointmentsByAppointmentActivityAndCreator(false, idLong));
            appointment.setTime(time);
            appointmentRepository.save(appointment);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(id);
            sendMessage.setText("Время успешно выбрано!");
            execute(sendMessage);
            if (appointment.getAppointmentType() != null) {
                if (appointment.getAppointmentType().equals(AppointmentType.KARAOKE)) {
                    execute(messager.getKaraokeAppointment(id, appointment));
                    user.setUserState(UserState.FINAL);
                    userMap.put(idLong, user);
                    userRepository.save(user);
                    execute(messager.bookTableFinal(id));
                } else {
                    user.setUserState(UserState.BOOKING);
                    userRepository.save(user);
                    userMap.put(idLong, user);
                    execute(messager.bookTableChoice(id));
                }
            }
        }
        if (callback.startsWith("tb") && user.getUserState().equals(UserState.BOOKING_TABLE)) {
            String table = callback.substring(2);
            Appointment appointment = clearAppointments(appointmentRepository.findAppointmentsByAppointmentActivityAndCreator(false, idLong));
            appointment.setTable_number(Integer.valueOf(table));
            appointmentRepository.save(appointment);

            execute(messager.sendAppointment(id, appointment));
            execute(messager.bookTableFinal(id));
            user.setUserState(UserState.FINAL);
            userRepository.save(user);
        }
        if(callback.startsWith("back")) {
            if (user.getUserState().equals(UserState.BOOKING)) {
                Appointment appointment = clearAppointments(appointmentRepository.findAppointmentsByAppointmentActivityAndCreator(false, idLong));
                appointmentRepository.delete(appointment);
                user.setUserState(UserState.START);
                userRepository.save(user);
                execute(messager.otherStart(id));
            }
            if(user.getUserState().equals(UserState.MENU)){
                execute(messager.otherStart(id));
                user.setUserState(UserState.START);
                userRepository.save(user);
            }
        }
        if (callback.startsWith("other_date")) {
            user.setUserState(UserState.OTHER_DATE);
            userRepository.save(user);
            userMap.put(idLong, user);
            execute(messager.other_date(id));
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBOT_NAME();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBOT_TOKEN();
    }

    public void expired() {
        List<Appointment> appointments = appointmentRepository.findAppointmentByAppointmentActivity(true);
        for (Appointment appointment : appointments) {
            Date createDate = appointment.getCreateDate();
            long createTime = createDate.getTime();
            long expiredTime = System.currentTimeMillis() - 8 * 60 * 60 * 1000L;
            if (createTime < expiredTime) {
                System.out.println("remove");
                appointmentRepository.delete(appointment);
            }
        }
    }

    private Appointment clearAppointments(List<Appointment> appointments) {
        if (!appointments.isEmpty()) {
            Appointment appointment = appointments.getLast();
            appointments.remove(appointment);
            appointmentRepository.deleteAll(appointments);

            return appointment;
        }
        return null;
    }
}
