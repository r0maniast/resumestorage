package ru.romankrivtsov.resume_storage.storage.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.romankrivtsov.resume_storage.model.Resume;
import ru.romankrivtsov.resume_storage.util.JsonParser;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonStreamSerializer implements StreamSerializer {
    private static final Logger log = LoggerFactory.getLogger(JsonStreamSerializer.class);

    @Override
    public void doWrite(Resume r, OutputStream os) throws IOException {
        log.debug("Starting JSON write of resume {}", r.getUuid());
        try (Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
            JsonParser.write(r, writer);
            log.debug("Finished JSON write of resume {}", r.getUuid());
        } catch (IOException e) {
            log.error("Error writing resume {} to JSON stream", r.getUuid(), e);
            throw e;
        }
    }

    @Override
    public Resume doRead(InputStream is) throws IOException {
        log.debug("Starting JSON read of resume from input stream");
        try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            Resume resume = JsonParser.read(reader, Resume.class);
            log.debug("Finished JSON read of resume {}", resume.getUuid());
            return resume;
        } catch (IOException e) {
            log.error("Error reading resume from JSON stream", e);
            throw e;
        }
    }
}
