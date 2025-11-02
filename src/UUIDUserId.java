import java.io.*;
import java.util.UUID;

public class UUIDUserId {
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void getOrCreateUserId() {
        File file = new File("user_id.txt");

        try {
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    this.userId = reader.readLine();
                }
            } else {
                this.userId = UUID.randomUUID().toString();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(this.userId);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при работе с файлом user_id.txt", e);
        }
    }
}