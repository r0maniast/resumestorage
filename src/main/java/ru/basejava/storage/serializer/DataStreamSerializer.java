package ru.basejava.storage.serializer;

import ru.basejava.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataStreamSerializer implements StreamSerializer {
    @Override
    public void doWrite(Resume r, OutputStream os) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(os)) {
            dos.writeUTF(r.getUuid());
            dos.writeUTF(r.getFullName());
            Map<ContactType, String> contacts = r.getContacts();
            dos.writeInt(contacts.size());
            for (Map.Entry<ContactType, String> entry : contacts.entrySet()) {
                dos.writeUTF(entry.getKey().name());
                dos.writeUTF(entry.getValue());
            }

            Map<SectionType, Section> sections = r.getSections();
            dos.writeInt(sections.size());
            for (Map.Entry<SectionType, Section> entry : sections.entrySet()) {
                SectionType type = entry.getKey();
                dos.writeUTF(type.name());
                if (type == SectionType.PERSONAL || type == SectionType.OBJECTIVE) {
                    dos.writeUTF(((TextSection) entry.getValue()).getContent());
                } else if (type == SectionType.ACHIEVEMENT || type == SectionType.QUALIFICATIONS) {
                    List<String> items = ((ListSection) entry.getValue()).getItems();
                    int sizeList = items.size();
                    dos.writeInt(sizeList);
                    for (String item : items) {
                        dos.writeUTF(item);
                    }
                } else if (type == SectionType.EDUCATION || type == SectionType.EXPERIENCE) {
                    List<Organization> organizationList = ((OrganizationSection) entry.getValue()).getOrganizations();
                    int sizeOrganizations = organizationList.size();
                    dos.writeInt(sizeOrganizations);
                    for (Organization org : organizationList) {
                        dos.writeUTF(org.getHomePage().getName());
                        dos.writeUTF(org.getHomePage().getURL());
                        List<Organization.Position> positions = org.getPositions();
                        int sizePositions = positions.size();
                        dos.writeInt(sizePositions);
                        for (Organization.Position position : positions) {
                            dos.writeUTF(position.getStartDate().toString());
                            dos.writeUTF(position.getEndDate().toString());
                            dos.writeUTF(position.getTitle());
                            dos.writeUTF(position.getDescription());
                        }
                    }
                }

            }
        }
    }

    @Override
    public Resume doRead(InputStream is) throws IOException {
        try (DataInputStream dis = new DataInputStream(is)) {
            String uuid = dis.readUTF();
            String fullName = dis.readUTF();
            Resume resume = new Resume(uuid, fullName);
            int sizeContacts = dis.readInt();
            for (int i = 0; i < sizeContacts; i++) {
                resume.addContact(ContactType.valueOf(dis.readUTF()), dis.readUTF());
            }

            int sizeSections = dis.readInt();
            for (int i = 0; i < sizeSections; i++) {
                String nameType = dis.readUTF();
                resume.addSection(SectionType.valueOf(nameType), readSection(dis, nameType));
            }

            return resume;
        }
    }

    private Section readSection(DataInputStream dis, String nameType) throws IOException {
        Section section = null;
        if (Objects.equals(nameType, SectionType.PERSONAL.name()) || Objects.equals(nameType, SectionType.OBJECTIVE.name())) {
            section = new TextSection(dis.readUTF());
        } else if (nameType.equals(SectionType.ACHIEVEMENT.name()) || nameType.equals(SectionType.QUALIFICATIONS.name())) {
            int sizeList = dis.readInt();
            List<String> list = new ArrayList<>();
            for (int i = 0; i < sizeList; i++) {
                list.add(dis.readUTF());
            }
            section = new ListSection(list);
        } else if (nameType.equals(SectionType.EDUCATION.name()) || nameType.equals(SectionType.EXPERIENCE.name())) {
            List<Organization> organizationList = new ArrayList<>();
            int sizeOrganizations = dis.readInt();
            for (int i = 0; i < sizeOrganizations; i++) {
                Link link = new Link(dis.readUTF(), dis.readUTF());
                List<Organization.Position> positionList = new ArrayList<>();
                int sizePositions = dis.readInt();

                for (int j = 0; j < sizePositions; j++) {
                    Organization.Position position = new Organization.Position(LocalDate.parse(dis.readUTF()),
                            LocalDate.parse(dis.readUTF()), dis.readUTF(), dis.readUTF());
                    positionList.add(position);
                }

                Organization org = new Organization(link, positionList);
                organizationList.add(org);
            }

            section = new OrganizationSection(organizationList);
        }
        return section;
    }
}

