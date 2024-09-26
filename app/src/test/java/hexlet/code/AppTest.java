package hexlet.code;

import hexlet.code.repository.UrlsRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    Javalin app;

    private static Path getFixturePath(String fileName) {
        return Paths.get("src", "test", "resources", "fixtures", fileName)
                .toAbsolutePath().normalize();
    }

    private static String readFixture(String fileName) throws Exception {
        Path filePath = getFixturePath(fileName);
        return Files.readString(filePath).trim();
    }

    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        app = App.getApp();
    }

    @Test
    public void testRootPage() {
        JavalinTest.test(app, ((server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body()).isNotNull();
            assertThat(response.body().string()).contains("Бесплатно проверяйте сайты на SEO пригодность");
        }));
    }

    @Test
    public void testInvalidUrl() {
        JavalinTest.test(app, ((server, client) -> {
            var requestBody = "url=invalid-url";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body()).isNotNull();
            assertThat(response.body().string()).contains("Бесплатно проверяйте сайты на SEO пригодность");
        }));
    }

    @Test
    public void testUrlNotFound() throws SQLException {
        Long id = 123456L;
        UrlsRepository.delete(id);
        JavalinTest.test(app, ((server, client) -> {
            var response = client.get("/urls/" + id);
            assertThat(response.code()).isEqualTo(404);
        }));
    }

}
