import java.io.InputStream;

public class Launcher {
    @lombok.SneakyThrows
    public static void main(String[] args) {
        Process proc = Runtime.getRuntime().exec("java -jar D:/Java/card-main/launcher/target/card-client-0.0.1-SNAPSHOT.jar");
        proc.waitFor();
        // Then retreive the process output
        InputStream in = proc.getInputStream();
        InputStream err = proc.getErrorStream();
        while (true) {
            byte b[] = new byte[in.available()];
            in.read(b, 0, b.length);
            System.out.print(new String(b));

            byte c[] = new byte[err.available()];
            err.read(c, 0, c.length);
            System.out.print(new String(c));

            Thread.sleep(500);

        }

    }
}
