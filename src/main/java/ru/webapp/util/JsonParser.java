package ru.webapp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webapp.model.Section;

import java.io.Reader;
import java.io.Writer;
import java.time.LocalDate;

public class JsonParser {
    private static final Logger log = LoggerFactory.getLogger(JsonParser.class);

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateJsonAdapter())
            .registerTypeAdapter(Section.class, new SectionJsonAdapter<>())
            .setPrettyPrinting()
            .create();

    public static <T> T read(Reader reader, Class<T> clazz) {
        log.debug("Reading JSON into class: {}", clazz.getSimpleName());
        T result = GSON.fromJson(reader, clazz);
        log.debug("Successfully parsed object: {}", result);
        return result;
    }

    public static <T> void write(T object, Writer writer) {
        log.debug("Writing object to JSON: {}", object);
        GSON.toJson(object, writer);
        log.debug("Successfully written object to JSON");
    }
}
