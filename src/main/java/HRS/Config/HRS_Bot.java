package HRS.Config;

import HRS.Service.Order;
import HRS.Service.Tool;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class HRS_Bot extends TelegramLongPollingBot {

    private static final String URL = "jdbc:sqlite:D:\\JavaFiles\\HamdenRentalSystem\\HamdenRentalSystem.db";
    Connection conn = DriverManager.getConnection(URL);
    private final DatabaseHandler database = new DatabaseHandler();
    public final String botName;
    public final String botToken;
    public static Map<String,String> usersData = new HashMap();
    public static Map<Tool, Integer> orderData = new HashMap(); //tools-hours аренда
    public static List<Tool> tools = new ArrayList<>();
    private static boolean isAdmin = false;
    private static boolean isUser = false;
    private static boolean isCourier = false;
    private static String currentCommand;          //стадия работы бота
    private static double TotalPrice;   //итоговая стоимость заказа всего
    private static boolean created = false;
    private static String currentUserID;
    private static String currentOrderID;
    private static String currentAddress;

    public HRS_Bot(String botName, String botToken) throws SQLException {
        this.botName = botName;
        this.botToken = botToken;
        usersData = database.userData();
        tools = database.catalog();
    }


    @Override
    public String getBotUsername() {
        return "HamdenRentalSystemBot";
    }

    @Override
    public String getBotToken() {
        return "6040622546:AAH9rzIPVnCNq8KaXAtFU6rP1ykbVHdhbzc";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            handleMessage(update.getMessage());
            if(currentCommand.equals("/signup")){
                signUp(update.getMessage());
                currentCommand = "";
            }
            if(currentCommand.equals("/login")){
                    logIn(update.getMessage());
                currentCommand = "";
            }
            if(currentCommand.equals("/addtocatalog")){
                addTool(update.getMessage());
                currentCommand = "";
            }
            if(currentCommand.equals("/createorder")){
                currentCommand = "";
            }
            if(currentCommand.equals("/address")){
                deliveryOrder(update.getMessage());
                currentCommand = "";
            }
            if(currentCommand.equals("/deletefromcatalog")){
                if(update.getMessage().getText().equals("stop")){
                    currentCommand = "";
                }else {
                    deleteTool(update.getMessage());
                }
            }
            if(currentCommand.equals("/Choose") && !update.getMessage().getText().equals("OK")){
                chooseReceiving(update.getMessage());
            }
            if(created){
                if(!update.getMessage().getText().equals("OK")) {
                    createOrder(update.getMessage());
                }else {
                    chooseReceivingPrint(update.getMessage());
                }

            }
        }
    }


    @SneakyThrows
    private void handleMessage(Message message) {
        if(message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity =
                    message
                            .getEntities()
                            .stream()
                            .filter(e -> "bot_command".equals(e.getType())).findFirst();
            if (commandEntity.isPresent()) {
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                switch (command) {
                    case "/catalog":
                        currentCommand = "/catalog";
                        execute(SendMessage
                                .builder()
                                .chatId(message.getChatId().toString())
                                .text("Каталог: \n" + formatCatalog(tools))
                                .build());
                        break;

                    case "/signup":
                        currentCommand = "/signup";
                        execute(SendMessage
                                .builder()
                                .chatId(message.getChatId().toString())
                                .text("Пожалуйста, введите логин, затем введите пароль (через пробел!!!!)")
                                .build());
                        break;
                    case "/login":
                        if(!isUser && !isAdmin && !isCourier) {
                            currentCommand = "/login";
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("Пожалуйста, введите логин, затем введите пароль (через пробел!!!!)")
                                    .build());
                        }else {
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("Выйдите из аккаунта.")
                                    .build());
                        }
                        break;
                    case "/logout":
                        isUser = false;
                        isAdmin = false;
                        isCourier = false;
                        execute(SendMessage
                                .builder()
                                .chatId(message.getChatId().toString())
                                .text("Вы вышли из аккаунта.")
                                .build());
                        break;
                    case "/showmyorder":
                        if(isUser){
                            currentCommand = "/showmyorder";
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("Ваш заказ: \n" + formatOrder(orderData) + "\n" + "Стоимость заказа: " + TotalPrice)
                                    .build());
                        }else {
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("У вас нет прав на эту команду.")
                                    .build());
                        }
                        break;
                    case "/deleteorder":
                        if(isUser){
                            if(!orderData.isEmpty()){
                                orderData.clear();
                                created = false;
                                TotalPrice = 0;
                                currentAddress = "";
                                execute(SendMessage
                                        .builder()
                                        .chatId(message.getChatId().toString())
                                        .text("Заказ удалён.")
                                        .build());
                            }else {
                                execute(SendMessage
                                        .builder()
                                        .chatId(message.getChatId().toString())
                                        .text("Вы не делали заказ.")
                                        .build());
                            }
                        }else {
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("У вас нет прав на эту команду.")
                                    .build());
                        }
                        break;
                    case "/deliveryaddress":
                        if(isCourier && !currentAddress.isEmpty()){
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("Адрес для доставки: " + currentAddress)
                                    .build());
                        }else {
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("У вас нет прав на эту команду.")
                                    .build());
                        }
                        break;
                    case "/createorder":
                        currentCommand = "/createorder";
                        created = true;
                        if(isUser) {
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("Введите номер инструмента из каталога и желаемое количество часов аренды (через пробел!!!!)")
                                    .build());
                        }else{
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("У вас нет прав на эту команду.")
                                    .build());
                        }
                        break;
                    case "/addtocatalog":
                        if(isAdmin) {
                            currentCommand = "/addtocatalog";
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("Введите тип инструмента(HAND_TOOL, GARDEN_TOOL, POWER_TOOL, MEASURING_TOOL), название, доставка(true,false) и цену инструмента (через пробел!!!!)")
                                    .build());

                        }else{
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("У вас нет прав на эту команду.")
                                    .build());
                        }
                        break;
                    case "/deletefromcatalog":
                        if(isAdmin) {
                            currentCommand = "/deletefromcatalog";
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("Введите номер инструмента из каталога, который нужно удалить")
                                    .build());
                        }else{
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("У вас нет прав на эту команду.")
                                    .build());
                        }
                        break;
                }
            }
        }
    }


    //Регистрация пользователя и добавление данных в бд
    @SneakyThrows
    private void signUp(Message message) {
        String logpas = message.getText();
        String login = logpas.split(" ")[0];
        String password = logpas.split(" ")[1];
        if (usersData.containsKey(login)) {
            execute(SendMessage
                    .builder()
                    .chatId(message.getChatId().toString())
                    .text("Пользователь с таким логином уже зарегистрирован.")
                    .build());
            currentCommand = "";
        } else {
            usersData.put(login, password);
            execute(SendMessage
                    .builder()
                    .chatId(message.getChatId().toString())
                    .text("Регистрация прошла успешно.")
                    .build());
            System.out.println(usersData.toString());
            currentCommand = "";
            try {
                Statement statement = conn.createStatement();
                String query = "INSERT INTO Userr (name, password) VALUES ('" + login + "', '" + password + "')";
                statement.executeUpdate(query);
                System.out.println("Данные успешно добавлены в базу данных");
            } catch (SQLException e) {
                System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
            }
        }
    }

    //Вход
    @SneakyThrows
    private void logIn(Message message) {
        String logpas = message.getText();
        String login = logpas.split(" ")[0];
        String password = logpas.split(" ")[1];
        for(Map.Entry<String, String> entry : usersData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            switch (login){
                case "Admin":
                    if(key.equals(login)){
                        if (value.equals(password) && password.equals("Admin")) {
                            isAdmin = true;
                            currentUserID = database.userID(login);
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("Добро пожаловать, администратор.")
                                    .build());
                        }else {
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("Неверный пароль.")
                                    .build());
                        }
                    }
                    break;

                case "Courier":
                    if(key.equals(login)){
                        if (value.equals(password) && password.equals("Courier")) {
                            isCourier = true;
                            currentUserID = database.userID(login);
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("Добро пожаловать, курьер.")
                                    .build());
                        }else {
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("Неверный пароль.")
                                    .build());
                        }
                    }
                    break;

                default:
                    if(key.equals(login)){
                        if (value.equals(password)) {
                            isUser = true;
                            currentUserID = database.userID(login);
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("Вход выполнен успешно.")
                                    .build());
                        }else {
                            execute(SendMessage
                                    .builder()
                                    .chatId(message.getChatId().toString())
                                    .text("Неверный пароль.")
                                    .build());
                        }
                    }
                    break;

            }
        }
        if(!usersData.containsKey(login)) {
            execute(SendMessage
                    .builder()
                    .chatId(message.getChatId().toString())
                    .text("Такого пользователя нет в системе.")
                    .build());
        }
    }

    //создание заказа
    @SneakyThrows
    private void createOrder(Message message){
            String toolHours = message.getText();
            String currentToolId = toolHours.split(" ")[0];
            int currentHours = Integer.parseInt(toolHours.split(" ")[1]);
            if(Integer.parseInt(currentToolId) > tools.size()){
                execute(SendMessage
                        .builder()
                        .chatId(message.getChatId().toString())
                        .text("id такого инструмента нет в каталоге.")
                        .build());
            } else {
                Double price = priceCalc(currentToolId, currentHours);
                TotalPrice += price;
                System.out.println(price);
                orderData.put(tools.get(Integer.parseInt(currentToolId)), currentHours);
                if (!currentToolId.isEmpty()) {
                    execute(SendMessage
                            .builder()
                            .chatId(message.getChatId().toString())
                            .text("Инструмент добавлен в заказ. Если это все, напишите ОК для подтверждения.")
                            .build());
                }
            }

    }


    //Просто вывод сообщения
    @SneakyThrows
    private void chooseReceivingPrint(Message message){
        execute(SendMessage
                .builder()
                .chatId(message.getChatId().toString())
                .text("""
                        Выберите метод получения.
                        1) - самовывоз\s
                        2) - доставка на дом""")
                .build());
        currentCommand = "/Choose";
    }

    //выбирается самовывоз или доставка. Если самовывоз, заказ создается и заносится в бд
    @SneakyThrows
    private void chooseReceiving(Message message){
        String mes = message.getText();
        switch (mes){
            case "1":
                Order order = new Order(TotalPrice,"Самовывоз");
                execute(SendMessage
                        .builder()
                        .chatId(message.getChatId().toString())
                        .text("Ваш заказ создан!")
                        .build());
                created = false;
                try {
                    Statement statement = conn.createStatement();
                    String query = "INSERT INTO Orderr (summary,receiving,address,fk_user_id) VALUES ('" + TotalPrice + "', '" + order.getReceiving() + "','" + null + "','" + currentUserID + "')";
                    statement.executeUpdate(query);
                    System.out.println("Данные успешно добавлены в orderr");
                } catch (SQLException e) {
                    System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
                }
                currentOrderID = database.orderID(currentUserID);
                for (Tool key : orderData.keySet()) {
                    try {
                        Statement statement = conn.createStatement();
                        String query = "INSERT INTO tool_order (fk_orderr,fk_tool) VALUES ('" + currentOrderID + "', '" + key.getId() + "')";
                        statement.executeUpdate(query);
                        System.out.println("Данные успешно добавлены в tool_order");
                    } catch (SQLException e) {
                        System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
                    }
                }
                break;
            case "2":
                boolean flag = true;
                for (Tool key : orderData.keySet()) {
                    if (!key.isDeliverable()) {
                        flag = false;
                    }
                }
                if(!flag){
                    execute(SendMessage
                            .builder()
                            .chatId(message.getChatId().toString())
                            .text("Доставка не доступна, так как в заказе присутствуют инструменты которые нельзя доставить. \n" + "Пожалуйста, введите цифру 1, чтобы выбрать самовывоз.")
                            .build());
                    currentCommand = "/Choose";
                }else {
                    execute(SendMessage
                            .builder()
                            .chatId(message.getChatId().toString())
                            .text("Пожалуйста, введите адрес проживания.")
                            .build());
                    currentCommand = "/address";
                }
                break;
        }
    }

    //заказ с доставкой
    @SneakyThrows
    private void deliveryOrder(Message message){
        String address = message.getText();
        currentAddress = address;
        Order orderD = new Order(TotalPrice,"Доставка",address);
        created = false;
        execute(SendMessage
                .builder()
                .chatId(message.getChatId().toString())
                .text("Ваш заказ создан!")
                .build());
        try {
            Statement statement = conn.createStatement();
            String query = "INSERT INTO Orderr (summary,receiving,address,fk_user_id) VALUES ('" + TotalPrice + "', '" + orderD.getReceiving() + "','" + address + "','" + currentUserID + "')";
            statement.executeUpdate(query);
            System.out.println("Данные успешно добавлены в orderr");
        } catch (SQLException e) {
            System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
        }
        currentOrderID = database.orderID(currentUserID);
        for (Tool key : orderData.keySet()) {
            try {
                Statement statement = conn.createStatement();
                String query = "INSERT INTO tool_order (fk_orderr,fk_tool) VALUES ('" + currentOrderID + "', '" + key.getId() + "')";
                statement.executeUpdate(query);
                System.out.println("Данные успешно добавлены в tool_order");
            } catch (SQLException e) {
                System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
            }
        }
    }

    //Удаление инструмента из базы данных
    @SneakyThrows
    private void deleteTool(Message message){
        String id = message.getText();
        System.out.println(id);
        if(id.matches("^\\d+$")){
            try {
                Statement statement = conn.createStatement();
                String query = "DELETE FROM Tool WHERE id = '" + id + "'";
                statement.executeUpdate(query);
                System.out.println("Данные успешно удалены из Tool");
            }catch (SQLException e) {
                System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
                execute(SendMessage
                        .builder()
                        .chatId(message.getChatId().toString())
                        .text("Ошибка. Возможно, такого id нет в базе данных.")
                        .build());
            }
            execute(SendMessage
                    .builder()
                    .chatId(message.getChatId().toString())
                    .text("Инструмент удалён! Если хотите удалить ещё, введите id. Либо же введите stop")
                    .build());
        }
    }

    //Добавление в каталог
    @SneakyThrows
    private void addTool(Message message){
        String type = message.getText().split(" ")[0];
        String name = message.getText().split(" ")[1];
        String deliv = message.getText().split(" ")[2];
        String price = message.getText().split(" ")[3];
        try {
            Statement statement = conn.createStatement();
            String query = "INSERT INTO Tool (tool_type,name,deliverable,price_per_hour) VALUES ('" + type + "', '" + name + "','" + deliv + "','" + price + "')";
            statement.executeUpdate(query);
            System.out.println("Данные успешно добавлены в Tool");
            execute(SendMessage
                    .builder()
                    .chatId(message.getChatId().toString())
                    .text("Данные успешно добавлены в Tool.")
                    .build());
        } catch (SQLException e) {
            System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
            execute(SendMessage
                    .builder()
                    .chatId(message.getChatId().toString())
                    .text("Ошибка при выполнении. Убедитесь в правильности ввода данных.")
                    .build());
        }
    }

    //Форматирование для каталога
    public String formatCatalog(List<Tool> tools){
        String cat = tools.toString().replace(",", "")
                .replace("[", "")
                .replace("true", "доступна")
                .replace("false", "не доступна")
                .replace("]", "")
                .replace("HAND_TOOL", "ручной")
                .replace("POWER_TOOL", "электрический")
                .replace("GARDEN_TOOL", "садовый")
                .replace("MEASURING_TOOL", "измерительный")
                .trim();
        return cat;
    }

    //Форматирование для заказа
    public String formatOrder(Map orderData){
        String cat = orderData.toString().replace(",", "")
                .replace("{", "")
                .replace("Доставка:", "")
                .replace("Тип:", "")
                .replace("true", "")
                .replace("false", "")
                .replace("}", "")
                .replace("HAND_TOOL", "")
                .replace("POWER_TOOL", "")
                .replace("GARDEN_TOOL", "")
                .replace("MEASURING_TOOL", "")
                .replace("=", "Количество часов в аренду: ")
                .trim();
        return cat;
    }

    //Вычисление стоимости одного инструмента
    public double priceCalc(String s, int i){
        return tools.get(Integer.parseInt(s)).getPricePerHour() * i;
    }

}


