package ru.webapp.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;

public class LocalDateJsonAdapter extends TypeAdapter<LocalDate> {
    private static final Logger log = LoggerFactory.getLogger(LocalDateJsonAdapter.class);

    @Override
    public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
        log.debug("Serializing LocalDate: {}", localDate);
        jsonWriter.value(localDate.toString());
    }

    @Override
    public LocalDate read(JsonReader jsonReader) throws IOException {
        String dateStr = jsonReader.nextString();
        log.debug("Deserializing LocalDate from string: {}", dateStr);
        LocalDate date = LocalDate.parse(dateStr);
        log.debug("Deserialized LocalDate: {}", date);
        return date;
    }
}
