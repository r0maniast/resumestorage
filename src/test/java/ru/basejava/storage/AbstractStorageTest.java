package ru.basejava.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.basejava.exception.ExistStorageException;
import ru.basejava.exception.NotExistStorageException;
import ru.basejava.model.*;

import java.time.Month;
import java.util.List;

public abstract class AbstractStorageTest {

    protected Storage storage;

    private static final String UUID_1 = "uuid1";
    private static final Resume R1;

    private static final String UUID_2 = "uuid2";
    private static final Resume R2;

    private static final String UUID_3 = "uuid3";
    private static final Resume R3;

    private static final String UUID_4 = "uuid4";
    private static final Resume R4;

    private static final Organization.Position POSITION_1 = new Organization.Position(2024, Month.SEPTEMBER, 2024, Month.DECEMBER,
            "Title1", "Description1");

    private static final Organization ORGANIZATION = new Organization("Name1", null, POSITION_1);

    private static final OrganizationSection ORGANIZATIONS = new OrganizationSection(List.of(ORGANIZATION));


    static {
        R1 = new Resume(UUID_1, "Name1");
        R2 = new Resume(UUID_2, "Name2");
        R3 = new Resume(UUID_3, "Name3");
        R4 = new Resume(UUID_4, "Name4");

        R1.addContact(ContactType.MAIL, "qwerty@mail.ru");
        R1.addContact(ContactType.MOBILE_PHONE, "+77777777777");
        R1.addSection(SectionType.PERSONAL, new TextSection("Personal data"));
        R1.addSection(SectionType.OBJECTIVE, new TextSection("Objective1"));
        R1.addSection(SectionType.ACHIEVEMENT, new ListSection("Achievement1", "Achievement12", "Achievement123"));
        R1.addSection(SectionType.QUALIFICATIONS, new ListSection("JAVA", "Git", "SQL"));
        R1.addSection(SectionType.EXPERIENCE,
                new OrganizationSection(List.of(
                        new Organization("Organization1", "https://Organization1.com",
                                new Organization.Position(2024, Month.SEPTEMBER, "position1", "Description1"),
                                new Organization.Position(2024, Month.MAY, 2024, Month.SEPTEMBER, "position2", "Description1")),
                        new Organization("Name2", "https://Name2.com",
                                new Organization.Position(2020, Month.SEPTEMBER, 2024, Month.APRIL, "Title2", "Description2")))));
        R1.addSection(SectionType.EDUCATION,
                new OrganizationSection(List.of(
                        new Organization("Institute1", "https://Institute1.com",
                                new Organization.Position(2024, Month.SEPTEMBER, "Title1", "Description1")),
                        new Organization("Institute2", "https://Institute2.com",
                                new Organization.Position(2020, Month.SEPTEMBER, 2024, Month.AUGUST, "Title2", "Description2")))));
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
        storage.update(newResume);
        Assertions.assertSame(newResume, storage.get(UUID_1));
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
        Assertions.assertThrows(NotExistStorageException.class, () -> storage.delete("dummy"));

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
        Assertions.assertThrows(NotExistStorageException.class, () -> storage.get("dummy"));

    }

    private void assertSize(int size) {
        Assertions.assertEquals(size, storage.size());
    }

    private void assertGet(Resume r) {
        Assertions.assertEquals(r, storage.get(r.getUuid()));
    }
}