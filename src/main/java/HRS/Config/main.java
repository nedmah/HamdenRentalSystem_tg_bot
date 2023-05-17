package HRS.Config;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.sql.SQLException;

public class main {

    public static void main(String[] args) {


        String token = "6040622546:AAH9rzIPVnCNq8KaXAtFU6rP1ykbVHdhbzc";
        String username = "HamdenRentalSystemBot";


        TelegramBotsApi telegramBotsApi = null;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new HRS_Bot(username,token));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
