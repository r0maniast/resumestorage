package ru.romankrivtsov.resume_storage.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.romankrivtsov.resume_storage.Config;
import ru.romankrivtsov.resume_storage.exception.ExistStorageException;
import ru.romankrivtsov.resume_storage.exception.NotExistStorageException;
import ru.romankrivtsov.resume_storage.model.*;

import java.io.File;
import java.time.Month;
import java.util.List;
import java.util.UUID;

public abstract class AbstractStorageTest {
    protected final static File STORAGE_DIR = Config.get().getStorageDir();
    protected Storage storage;

    private static final String UUID_1 = UUID.randomUUID().toString();
    private static final String UUID_2 = UUID.randomUUID().toString();
    private static final String UUID_3 = UUID.randomUUID().toString();
    private static final String UUID_4 = UUID.randomUUID().toString();

    private static final Resume R1;
    private static final Resume R2;
    private static final Resume R3;
    private static final Resume R4;

    static {
        R1 = new Resume(UUID_1, "Name1");
        R2 = new Resume(UUID_2, "Name2");
        R3 = new Resume(UUID_3, "Name3");
        R4 = new Resume(UUID_4, "Name4");

        R1.setContact(ContactType.MAIL, "qwerty@mail.ru");
        R1.setContact(ContactType.MOBILE_PHONE, "+77777777777");
        R1.setSection(SectionType.PERSONAL, new TextSection("Personal data"));
        R1.setSection(SectionType.OBJECTIVE, new TextSection("Objective1"));
        R1.setSection(SectionType.ACHIEVEMENT, new ListSection("Achievement1", "Achievement12", "Achievement123"));
        R1.setSection(SectionType.QUALIFICATIONS, new ListSection("JAVA", "Git", "SQL"));
        R1.setSection(SectionType.EXPERIENCE,
                new OrganizationSection(List.of(
                        new Organization("Organization1", "https://Organization1.com",
                                new Organization.Position(2024, Month.SEPTEMBER, "Title1", "Description1"),
                                new Organization.Position(2024, Month.MAY, 2024, Month.SEPTEMBER, "Title2", "Description1")),
                        new Organization("Organization2", "https://Name2.com",
                                new Organization.Position(2020, Month.SEPTEMBER, 2024, Month.APRIL, "Title1", "Description2")))));
        R1.setSection(SectionType.EDUCATION,
                new OrganizationSection(List.of(
                        new Organization("Institute1", "https://Institute1.com",
                                new Organization.Position(2024, Month.SEPTEMBER, "Title1", "Description1")),
                        new Organization("Institute2", "https://Institute2.com",
                                new Organization.Position(2020, Month.SEPTEMBER, 2024, Month.AUGUST, "Title2", "Description2")))));
        R2.setContact(ContactType.MAIL, "asdfgh@mail.ru");
        R2.setContact(ContactType.MOBILE_PHONE, "+78945612378");
        R2.setSection(SectionType.EXPERIENCE,
                new OrganizationSection(List.of(
                        new Organization("Organization1", "https://Organization1.com",
                                new Organization.Position(2020, Month.SEPTEMBER, "Title1", "Description1")))));
    }


    protected AbstractStorageTest(Storage storage) {
        this.storage = storage;
    }

    @BeforeEach
    public void setUp() {
        storage.clear();
        storage.save(R1);
        storage.save(R2);
        storage.save(R3);
    }

    @Test
    void size() {
        assertSize(3);
    }

    @Test
    void clear() {
        storage.clear();
        assertSize(0);
    }

    @Test
    void update() {
        Resume newResume = new Resume(UUID_1, "NewName");
        newResume.setContact(ContactType.MAIL, "ytrewq@mail.ru");
        newResume.setContact(ContactType.MOBILE_PHONE, "+1234567");
        newResume.setSection(SectionType.EXPERIENCE,
                new OrganizationSection(
                        new Organization("NewORG", "NewURL",
                                new Organization.Position(2000, Month.APRIL, 2010, Month.MAY, "NewPos", "NewDesc"))));
        storage.update(newResume);
        Assertions.assertEquals(newResume, storage.get(UUID_1));
    }

    @Test
    void updateNotExist() {
        Assertions.assertThrows(NotExistStorageException.class, () -> {
            Resume newResume = new Resume("dummy");
            storage.update(newResume);
        });
    }

    @Test
    void save() {
        storage.save(R4);
        assertSize(4);
        assertGet(R4);
    }

    @Test
    void saveExist() {
        Assertions.assertThrows(ExistStorageException.class, () -> storage.save(R1));
    }

    @Test
    void delete() {
        Assertions.assertThrows(NotExistStorageException.class, () -> {
            storage.delete(UUID_1);
            assertSize(2);
            storage.get(UUID_1);
        });
    }

    @Test
    void deleteNotExist() {
        Assertions.assertThrows(NotExistStorageException.class, () -> storage.delete(UUID.randomUUID().toString()));

    }

    @Test
    void getAllSorted() {
        List<Resume> expected = List.of(R1, R2, R3);
        List<Resume> storageAllSorted = storage.getAllSorted();
        for (int i = 0; i < storage.size(); i++) {
            Assertions.assertEquals(expected.get(i), storageAllSorted.get(i));
        }

    }

    @Test
    void get() {
        assertGet(R1);
        assertGet(R2);
        assertGet(R3);
    }

    @Test
    void getNotExist() {
        Assertions.assertThrows(NotExistStorageException.class, () -> storage.get(UUID.randomUUID().toString()));

    }

    private void assertSize(int size) {
        Assertions.assertEquals(size, storage.size());
    }

    private void assertGet(Resume r) {
        Assertions.assertEquals(r, storage.get(r.getUuid()));
    }
}