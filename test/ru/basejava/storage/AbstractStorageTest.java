package ru.basejava.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.basejava.exception.ExistStorageException;
import ru.basejava.exception.NotExistStorageException;
import ru.basejava.model.Resume;

import java.util.List;

public abstract class AbstractStorageTest {
    protected Storage storage;
    private static final String UUID_1 = "uuid1";
    private static final String FULL_NAME_1 = "Name1";
    private static final Resume RESUME_1 = new Resume(UUID_1, FULL_NAME_1);

    private static final String UUID_2 = "uuid2";
    private static final String FULL_NAME_2 = "Name2";

    private static final Resume RESUME_2 = new Resume(UUID_2, FULL_NAME_2);

    private static final String UUID_3 = "uuid3";
    private static final String FULL_NAME_3 = "Name3";

    private static final Resume RESUME_3 = new Resume(UUID_3, FULL_NAME_3);

    private static final String UUID_4 = "uuid4";
    private static final String FULL_NAME_4 = "Name4";

    private static final Resume RESUME_4 = new Resume(UUID_4, FULL_NAME_4);

    protected AbstractStorageTest(Storage storage) {
        this.storage = storage;
    }

    @BeforeEach
    public void setUp() {
        storage.clear();
        storage.save(RESUME_1);
        storage.save(RESUME_2);
        storage.save(RESUME_3);
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
        Resume newResume = new Resume(UUID_1, FULL_NAME_1);
        storage.update(newResume);
        Assertions.assertTrue(newResume == storage.get(UUID_1));
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
        storage.save(RESUME_4);
        assertSize(4);
        assertGet(RESUME_4);
    }

    @Test
    void saveExist() {
        Assertions.assertThrows(ExistStorageException.class, () -> {
            storage.save(RESUME_1);
        });
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
        List<Resume> expected = List.of(RESUME_1, RESUME_2, RESUME_3);
        List<Resume> storageAllSorted = storage.getAllSorted();
        for (int i = 0; i < storage.size(); i++) {
            Assertions.assertEquals(expected.get(i),storageAllSorted.get(i));
        }

    }

    @Test
    void get() {
        assertGet(RESUME_1);
        assertGet(RESUME_2);
        assertGet(RESUME_3);
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