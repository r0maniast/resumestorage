package ru.romankrivtsov.resume_storage.storage.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.romankrivtsov.resume_storage.model.*;
import ru.romankrivtsov.resume_storage.util.XmlParser;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class XmlStreamSerializer implements StreamSerializer {
    private static final Logger log = LoggerFactory.getLogger(XmlStreamSerializer.class);
    private final XmlParser xmlParser;

    public XmlStreamSerializer() {
        log.info("Initializing XmlStreamSerializer with XmlParser for Resume and related classes");
        xmlParser = new XmlParser(
                Resume.class, Organization.class, Link.class,
                OrganizationSection.class, TextSection.class, Section.class,
                Organization.Position.class, ListSection.class);
        log.debug("XmlParser initialized: {}", xmlParser.getClass().getSimpleName());
    }

    @Override
    public void doWrite(Resume r, OutputStream os) throws IOException {
        log.debug("Starting XML write of resume {}", r.getUuid());
        try (Writer w = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
            xmlParser.marshall(r, w);
            log.debug("Finished XML write of resume {}", r.getUuid());
        } catch (IOException e) {
            log.error("Error writing resume {} to XML stream", r.getUuid(), e);
            throw e;
        }
    }

    @Override
    public Resume doRead(InputStream is) throws IOException {
        log.debug("Starting XML read of resume from input stream");
        try (Reader r = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            Resume resume = xmlParser.unmarshall(r);
            log.debug("Finished XML read of resume {}", resume.getUuid());
            return resume;
        } catch (IOException e) {
            log.error("Error reading resume from XML stream", e);
            throw e;
        }
    }
}
