package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import hexlet.code.dto.MainPage;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Collections;

@Slf4j
public class App {

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }

//    private static String readResourceFile(String fileName) throws IOException {
//        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
//            return reader.lines().collect(Collectors.joining("\n"));
//        }
//    }

    public static void main(String[] args) throws IOException, SQLException {
        var app = getApp();

        app.start(getPort());
    }

    public static Javalin getApp() throws IOException, SQLException {
        // System.setProperty("h2.traceLevel", "TRACE_LEVEL_SYSTEM_OUT=4");

        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");

//        var dataSource = new HikariDataSource(hikariConfig);
//        var sql = readResourceFile("schema.sql");

//        log.info(sql);
//        try (var connection = dataSource.getConnection();
//             var statement = connection.createStatement()) {
//            statement.execute(sql);
//        }

        Path templateRoot = Path.of("src/main/resources/templates");
        TemplateEngine templateEngine = TemplateEngine.create(new DirectoryCodeResolver(templateRoot),
                ContentType.Html);


        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(templateEngine));
        });

        app.before(ctx -> {
            ctx.contentType("text/html; charset=utf-8");
        });

        app.get("/", ctx -> {
            var helloString = "Hello World!";
            var page = new MainPage(helloString);
            ctx.render("index.jte", Collections.singletonMap("page", page));
        });





        return app;
    }



}
