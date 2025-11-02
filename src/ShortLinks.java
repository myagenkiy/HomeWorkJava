import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ShortLinks {
    public ShortLinks() {
        loadLinks();
    }

    private final String SYMBOLS = "qwertyuiopasdfghjklzxcvbnm1234567890"; // символы для генерации короткой ссылки
    private final Map<String, Map<String, LinkData>> storage = new HashMap<>();
    private final File file = new File("links.txt");

    private final int MAX_HITS = 10; // количество переходов по ссылке
    private final long LINK_LIFETIME = 7200000; // время жизни короткой ссылки в миллисекундах

    private String generateCode(String user, int length) {
        int hash = Math.abs(user.hashCode());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(SYMBOLS.charAt(hash % SYMBOLS.length()));
            hash /= SYMBOLS.length();
        }
        return sb.toString();
    }

    public String shorten(String userId, String longUrl) {
        String userAndUrl = userId + " " + longUrl;
        String code = generateCode(userAndUrl, 5);

        storage.putIfAbsent(userId, new HashMap<>());
        storage.get(userId).put(code, new LinkData(longUrl, MAX_HITS, System.currentTimeMillis()));

        saveLinks();
        return code;
    }

    public void urlInfo(String userId) {
        Map<String, LinkData> userLinks = storage.get(userId);

        if (userLinks == null || userLinks.isEmpty()) {
            System.out.println("Нет сохраненных ссылок");
            return;
        }

        long currentTime = System.currentTimeMillis();

        for (Map.Entry<String, LinkData> entry : userLinks.entrySet()) {
            LinkData data = entry.getValue();
            long timeLeft = LINK_LIFETIME - (currentTime - data.createTime);

            if (timeLeft <= 0 || data.hits <= 0) {
                userLinks.remove(entry.getKey());
                System.out.println("Нет сохраненных ссылок");
                continue;
            }

            System.out.println("Короткая ссылка: " + entry.getKey() +
                    " > Длинная ссылка: " + entry.getValue().longUrl);
        }
        saveLinks();
    }

    public void linkInfo(String userId, String code) {
        Map<String, LinkData> userLinks = storage.get(userId);
        if (userLinks == null || !userLinks.containsKey(code)) {
            System.out.println("Ссылка не найдена или устарела");
            return;
        }

        LinkData data = userLinks.get(code);
        long timeLeft = LINK_LIFETIME - (System.currentTimeMillis() - data.createTime);

        if (timeLeft <= 0) {
            System.out.println("Время жизни ссылки истекло");
            userLinks.remove(code);
            saveLinks();
            return;
        }

        long hours = timeLeft / (1000 * 60 * 60);
        long minutes = (timeLeft / (1000 * 60)) % 60;
        long seconds = (timeLeft / 1000) % 60;

        System.out.println("Осталось переходов: " + data.hits);
        System.out.println("Осталось времени: " + hours + "ч " + minutes + "мин " + seconds + "сек.");
    }

    public String goInLink(String userId, String code) {
        Map<String, LinkData> userLinks = storage.get(userId);
        if (userLinks == null || !userLinks.containsKey(code)) {
            System.out.println("Ссылка не найдена или устарела");
            System.out.println();
            return null;
        }

        LinkData data = userLinks.get(code);
        long currentTime = System.currentTimeMillis();

        if (currentTime - data.createTime > LINK_LIFETIME) {
            System.out.println("Время жизни ссылки истекло");
            System.out.println();
            userLinks.remove(code);
            saveLinks();
            return null;
        }

        if (data.hits <= 0) {
            System.out.println("Закончилось количество переходов по ссылке");
            System.out.println();
            userLinks.remove(code);
            saveLinks();
            return null;
        }

        data.hits--;
        saveLinks();
        return data.longUrl;
    }

    private void saveLinks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<String, Map<String, LinkData>> userEntry : storage.entrySet()) {
                String userId = userEntry.getKey();
                for (Map.Entry<String, LinkData> entry : userEntry.getValue().entrySet()) {
                    LinkData data = entry.getValue();
                    writer.write(userId + "=" + entry.getKey() + "," +
                            data.longUrl + "," + data.hits + "," + data.createTime);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении ссылок");
        }
    }

    private void loadLinks() {
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String userId = parts[0];
                    String[] values = parts[1].split(",", 4);
                    if (values.length == 4) {
                        String code = values[0];
                        String longUrl = values[1];
                        int hits = Integer.parseInt(values[2]);
                        long createTime = Long.parseLong(values[3]);

                        storage.putIfAbsent(userId, new HashMap<>());
                        storage.get(userId).put(code, new LinkData(longUrl, hits, createTime));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке ссылок");
        }
    }

    private static class LinkData {
        String longUrl;
        int hits;
        long createTime;

        public LinkData(String longUrl, int hits, long createTime) {
            this.longUrl = longUrl;
            this.hits = hits;
            this.createTime = createTime;
        }
    }
}