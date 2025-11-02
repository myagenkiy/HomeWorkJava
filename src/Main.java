import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ShortLinks shortLinks = new ShortLinks();
        UUIDUserId uuidUserId = new UUIDUserId();

        uuidUserId.getOrCreateUserId();

        System.out.println("Ваш id пользователя:\n" + uuidUserId.getUserId());
        System.out.println();

        while (true) {
            System.out.println("------Выберите действие------");
            System.out.println(" ");
            System.out.println("1. Ввести длинную ссылку и получить короткую\n" +
                    "2. Посмотреть все короткие ссылки\n" +
                    "3. Узнать сколько осталось переходов и времени жизни ссылки\n" +
                    "4. Перейти по короткой ссылке\n" +
                    "5. Выйти из программы");

            System.out.println();
            System.out.println("Выберите номер действия");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("Введите длинную ссылку: ");
                    String longUrl = scanner.nextLine();
                    String shortUrl = shortLinks.shorten(uuidUserId.getUserId(), longUrl);
                    System.out.println("Ваша короткая ссылка: " + shortUrl);
                    System.out.println();
                    break;
                case "2":
                    shortLinks.urlInfo(uuidUserId.getUserId());
                    System.out.println();
                    break;
                case "3":
                    System.out.println("Введите короткую ссылку для проверки: ");
                    String shortRl = scanner.nextLine();
                    shortLinks.linkInfo(uuidUserId.getUserId(), shortRl);
                    System.out.println();
                    break;
                case "4":
                    System.out.println("Введите короткую ссылку для перехода: ");
                    String shortU = scanner.nextLine();
                    try {
                        String fullUrl = shortLinks.goInLink(uuidUserId.getUserId(), shortU);
                        if (fullUrl != null)
                            Desktop.getDesktop().browse(new URI(fullUrl));
                    } catch (IOException | URISyntaxException e) {
                        System.out.println("Ошибка при открытии ссылки");
                        System.out.println();
                    }
                    break;
                case "5":
                    System.out.println("Выход из программы...");
                    return;
                default:
                    System.out.println("Неверно введен номер действия...");
                    System.out.println();
            }
        }
    }
}