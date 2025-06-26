package ru.webapp.storage.serializer;

import ru.webapp.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DataStreamSerializer implements StreamSerializer {
    @Override
    public void doWrite(Resume r, OutputStream os) throws IOException {
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
                    case PERSONAL, OBJECTIVE -> dos.writeUTF(((TextSection) section).getContent());

                    case ACHIEVEMENT, QUALIFICATIONS ->
                            writeCollection(dos, ((ListSection) section).getItems(), dos::writeUTF);

                    case EXPERIENCE, EDUCATION ->
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
                }
            });
        }
    }

    @Override
    public Resume doRead(InputStream is) throws IOException {
        try (DataInputStream dis = new DataInputStream(is)) {
            String uuid = dis.readUTF();
            String fullName = dis.readUTF();
            Resume resume = new Resume(uuid, fullName);
            readItems(dis, () -> resume.setContact(ContactType.valueOf(dis.readUTF()), dis.readUTF()));

            readItems(dis, () -> {
                        SectionType type = SectionType.valueOf(dis.readUTF());
                        resume.setSection(type,
                                switch (type) {
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
                                });
                    }
            );
            return resume;
        }
    }

    protected interface ElementProcessor {
        void process() throws IOException;
    }

    protected interface ElementReader<T> {
        T read() throws IOException;
    }

    private interface ElementWriter<T> {
        void write(T t) throws IOException;
    }

    private LocalDate readLocalDate(DataInputStream dis) throws IOException {
        return LocalDate.of(dis.readInt(), dis.readInt(), 1);
    }

    private void writeLocalDate(DataOutputStream dos, LocalDate ld) throws IOException {
        dos.writeInt(ld.getYear());
        dos.writeInt(ld.getMonthValue());
    }

    private <T> List<T> readList(DataInputStream dis, ElementReader<T> reader) throws IOException {
        int size = dis.readInt();
        List<T> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(reader.read());
        }
        return list;
    }

    private void readItems(DataInputStream dis, ElementProcessor processor) throws IOException {
        int size = dis.readInt();
        for (int i = 0; i < size; i++) {
            processor.process();
        }
    }

    private <T> void writeCollection(DataOutputStream dos, Collection<T> collection, ElementWriter<T> writer) throws IOException {
        dos.writeInt(collection.size());
        for (T item : collection) {
            writer.write(item);
        }
    }
}

