import java.io.InputStream;

public class MainTest {
    public static void main(String[] args) {
        InputStream stream = MainTest.class.getResourceAsStream("/images/core.png");
        if (stream == null) {
            System.out.println("❌ core.png not found");
        } else {
            System.out.println("✅ core.png found");
        }
    }
}
