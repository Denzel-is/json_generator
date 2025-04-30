package com.disgroup.pipeline.json.generator;

import com.disgroup.pipeline.json.generator.model.AttributeSet;
import com.disgroup.pipeline.json.generator.model.RecordItem;
import com.disgroup.pipeline.json.generator.model.RecordsWrapper;
import com.disgroup.pipeline.json.generator.util.ConsoleInput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class JsonGeneratorApplication implements CommandLineRunner {

    private final ObjectMapper mapper = new ObjectMapper()
            .disable(SerializationFeature.INDENT_OUTPUT);

    /* Имя подкаталога, куда будут складываться файлы */
    private static final String RESOURCE_DIR = "resources";          // для jar
    private static final String SRC_RES_DIR = "src/main/resources";  // для IDE
    private static final String FILE_PREFIX = "custom_testref_records";
    private static final String FILE_EXT    = ".json";

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(JsonGeneratorApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Override
    public void run(String... args) {
        ConsoleInput in = new ConsoleInput();

        /* ===== 1. мини-меню ===== */
        System.out.println("=== Генератор JSON-записей ===");
        System.out.println("1 — attr1 / attr2 + префикс my_unique_id");
        System.out.println("2 — задать названия атрибутов и префикс externalId");
        int mode = in.askInt("Ваш выбор:", 1, v -> v == 1 || v == 2);

        String attr1Key  = "attr1";
        String attr2Key  = "attr2";
        String extPrefix = "my_unique_id";


        if (mode == 2) {
            attr1Key  = in.askString("Название первого атрибута:",  attr1Key).trim();
            attr2Key  = in.askString("Название второго атрибута:",  attr2Key).trim();
            extPrefix = in.askString("Новый префикс externalId:",   extPrefix).trim();
        }

        /* ===== 2. входная строка ===== */
        System.out.printf(
                "%nВведите через запятую: <начальный %s>,<база %s>,<кол-во записей>%n",
                attr1Key, attr2Key
        );
        System.out.println("Пример: 0000001,Тестовая запись,5");
        System.out.print("> ");

        String[] parts = in.nextLine().split(",", 3);
        while (parts.length < 3) {
            System.out.print("Нужно три значения. Повторите: ");
            parts = in.nextLine().split(",", 3);
        }
        String attr1StartStr = parts[0].trim();
        String attr2Base     = parts[1].trim().isEmpty()
                ? "Тестовая запись" : parts[1].trim();
        int count = 1;
        try { count = Integer.parseInt(parts[2].trim()); }
        catch (NumberFormatException ignored) {}

        int pad        = attr1StartStr.length();
        int attr1Start = Integer.parseInt(attr1StartStr);

        /* ===== 3. формирование записей ===== */
        List<RecordItem> items = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int seq          = i + 1;
            String externalId = extPrefix + seq;
            String attr1Val   = String.format("%0" + pad + "d", attr1Start + i);
            String attr2Val   = attr2Base + ' ' + seq;

            AttributeSet attrs = new AttributeSet()
                    .set(attr1Key, attr1Val)
                    .set(attr2Key, attr2Val);

            items.add(new RecordItem(externalId, attrs));
        }

        RecordsWrapper wrapper = new RecordsWrapper("TestRef", items);

        /* ===== 4. вычисляем уникальное имя файла ===== */
        Path dir = detectResourceDir();
        try { Files.createDirectories(dir); } catch (IOException ignored) {}

        int nextIdx = findNextIndex(dir);
        String fileName = FILE_PREFIX + nextIdx + FILE_EXT;
        Path outPath = dir.resolve(fileName);

        /* ===== 5. сериализация и сохранение ===== */
        try {
            String json = mapper.writeValueAsString(wrapper);

            System.out.println("\n---- Итоговый JSON ----");
            System.out.println(json);

            Files.writeString(outPath, json);
            System.out.println("\nФайл сохранён: " + outPath.toAbsolutePath());
        } catch (JsonProcessingException e) {
            System.err.println("Ошибка сериализации: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка записи файла: " + e.getMessage());
        }
    }

    /** Определяем, какой каталог ресурсов доступен в окружении. */
    private Path detectResourceDir() {
        Path srcPath = Paths.get(SRC_RES_DIR);
        if (Files.exists(srcPath)) return srcPath;               // запуск из IDE/Gradle
        return Paths.get(RESOURCE_DIR);                          // запуск из jar
    }

    /** Смотрим, сколько файлов уже есть, чтобы выбрать следующий индекс. */
    private int findNextIndex(Path dir) {
        try (Stream<Path> files = Files.list(dir)) {
            return files
                    .filter(p -> p.getFileName().toString().startsWith(FILE_PREFIX))
                    .map(p -> p.getFileName().toString())
                    .map(name -> name.replace(FILE_PREFIX, "").replace(FILE_EXT, ""))
                    .filter(s -> s.matches("\\d+"))
                    .map(Integer::parseInt)
                    .max(Comparator.naturalOrder())
                    .orElse(0) + 1;
        } catch (IOException e) {
            return 1;
        }
    }
}
