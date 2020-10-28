import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GenerateLogs {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
    private static final List<String> USERS = Arrays.asList("james", "frank", "mary", "jill");
    private static final List<String> VERBS = Arrays.asList("PUT", "POST", "GET");
    private static final List<String> URLS = Arrays.asList("/report", "/api/user", "/api/data", "/health", "/info");
    private static final List<String> CODE = Arrays.asList("200", "403", "500");

    public static void main(String[] args) throws IOException {
        File file = new File("/Users/glebiller/Workspace/guillaume/calog/target/access.log");
        try (FileWriter writer = new FileWriter(file)) {
            while (true) {
                Random random = new Random();
                Thread.sleep(Math.round(random.nextInt(20000)));
                writer.write(String.format("127.0.0.1 - %s [%s] \"%s %s HTTP/1.0\" %s %d\n",
                        USERS.get(Math.round(random.nextInt(USERS.size()))),
                        SDF.format(new Date()),
                        VERBS.get(Math.round(random.nextInt(VERBS.size()))),
                        URLS.get(Math.round(random.nextInt(URLS.size()))),
                        CODE.get(Math.round(random.nextInt(CODE.size()))),
                        random.nextInt(1000) + 100
                ));
                writer.flush();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
