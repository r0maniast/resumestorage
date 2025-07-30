package ru.romankrivtsov.resume_storage.storage.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.romankrivtsov.resume_storage.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DataStreamSerializer implements StreamSerializer {
    private static final Logger log = LoggerFactory.getLogger(DataStreamSerializer.class);

    @Override
    public void doWrite(Resume r, OutputStream os) throws IOException {
        log.debug("Starting write of resume {} to output stream", r.getUuid());
        try (DataOutputStream dos = new DataOutputStream(os)) {
            dos.writeUTF(r.getUuid());
            dos.writeUTF(r.getFullName());
            writeCollection(dos, r.getContacts().entrySet(), entry -> {
                dos.writeUTF(entry.getKey().name());
                dos.writeUTF(entry.getValue());
            });
            writeCollection(dos, r.getSections().entrySet(), entry -> {
                SectionType type = entry.getKey();
                Section section = entry.getValue();
                dos.writeUTF(type.name());
                switch (type) {
                    case PERSONAL, OBJECTIVE -> {
                        dos.writeUTF(((TextSection) section).getContent());
                        log.trace("Wrote TextSection {} content=...", type);
                    }
                    case ACHIEVEMENT, QUALIFICATIONS -> {
                        writeCollection(dos, ((ListSection) section).getItems(), dos::writeUTF);
                        log.trace("Wrote ListSection {} items count={}", type, ((ListSection) section).getItems().size());
                    }
                    case EXPERIENCE, EDUCATION -> {
                        writeCollection(dos, ((OrganizationSection) section).getOrganizations(), org -> {
                            dos.writeUTF(org.getHomePage().getName());
                            dos.writeUTF(org.getHomePage().getUrl());
                            writeCollection(dos, org.getPositions(), pos -> {
                                writeLocalDate(dos, pos.getStartDate());
                                writeLocalDate(dos, pos.getEndDate());
                                dos.writeUTF(pos.getTitle());
                                dos.writeUTF(pos.getDescription());
                            });
                        });
                        log.trace("Wrote OrganizationSection {} org count={}", type, ((OrganizationSection) section).getOrganizations().size());
                    }
                }
            });
            log.debug("Finished writing resume {} to output stream", r.getUuid());
        } catch (IOException e) {
            log.error("Error writing resume {} to output stream", r.getUuid(), e);
            throw e;
        }
    }

    @Override
    public Resume doRead(InputStream is) throws IOException {
        log.debug("Starting read of resume from input stream");
        try (DataInputStream dis = new DataInputStream(is)) {
            String uuid = dis.readUTF();
            String fullName = dis.readUTF();
            Resume resume = new Resume(uuid, fullName);
            log.trace("Read resume header uuid={} fullName={}", uuid, fullName);
            readItems(dis, () -> {
                ContactType type = ContactType.valueOf(dis.readUTF());
                String value = dis.readUTF();
                resume.setContact(type, value);
                log.trace("Read contact {}={}", type, value);
            });
            readItems(dis, () -> {
                SectionType type = SectionType.valueOf(dis.readUTF());
                Section section = switch (type) {
                    case PERSONAL, OBJECTIVE -> new TextSection(dis.readUTF());
                    case ACHIEVEMENT, QUALIFICATIONS -> new ListSection(readList(dis, dis::readUTF));
                    case EXPERIENCE, EDUCATION -> new OrganizationSection(
                            readList(dis, () -> new Organization(
                                    new Link(dis.readUTF(), dis.readUTF()),
                                    readList(dis, () -> new Organization.Position(
                                            readLocalDate(dis),
                                            readLocalDate(dis),
                                            dis.readUTF(),
                                            dis.readUTF())))));
                };
                resume.setSection(type, section);
                log.trace("Read section {}", type);
            });
            log.debug("Finished reading resume {} from input stream", resume.getUuid());
            return resume;
        } catch (IOException e) {
            log.error("Error reading resume from input stream", e);
            throw e;
        }
    }

    protected interface ElementProcessor {
        void process() throws IOException;
    }

    protected interface ElementReader<T> {
        T read() throws IOException;
    }

    protected interface ElementWriter<T> {
        void write(T t) throws IOException;
    }

    private LocalDate readLocalDate(DataInputStream dis) throws IOException {
        int year = dis.readInt();
        int month = dis.readInt();
        LocalDate ld = LocalDate.of(year, month, 1);
        log.trace("Read LocalDate year={} month={} -> {}", year, month, ld);
        return ld;
    }

    private void writeLocalDate(DataOutputStream dos, LocalDate ld) throws IOException {
        dos.writeInt(ld.getYear());
        dos.writeInt(ld.getMonthValue());
        log.trace("Wrote LocalDate {}", ld);
    }

    private <T> List<T> readList(DataInputStream dis, ElementReader<T> reader) throws IOException {
        int size = dis.readInt();
        log.trace("Reading list of size={}", size);
        List<T> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(reader.read());
        }
        return list;
    }

    private void readItems(DataInputStream dis, ElementProcessor processor) throws IOException {
        int size = dis.readInt();
        log.trace("Reading {} items", size);
        for (int i = 0; i < size; i++) {
            processor.process();
        }
    }

    private <T> void writeCollection(DataOutputStream dos, Collection<T> collection, ElementWriter<T> writer) throws IOException {
        dos.writeInt(collection.size());
        log.trace("Writing collection of size={}", collection.size());
        for (T item : collection) {
            writer.write(item);
        }
    }
}
