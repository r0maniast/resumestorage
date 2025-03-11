package ru.javawebinar.basejava.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.javawebinar.basejava.exception.ExistStorageException;
import ru.javawebinar.basejava.exception.NotExistStorageException;
import ru.javawebinar.basejava.exception.StorageException;
import ru.javawebinar.basejava.model.Resume;

public abstract class AbstractArrayStorageTest {
    private Storage storage;
    private static final String UUID_1 = "uuid1";
    private static final Resume RESUME_1 = new Resume(UUID_1);
    private static final String UUID_2 = "uuid2";
    private static final Resume RESUME_2 = new Resume(UUID_2);
    private static final String UUID_3 = "uuid3";
    private static final Resume RESUME_3 = new Resume(UUID_3);
    private static final String UUID_4 = "uuid4";
    private static final Resume RESUME_4 = new Resume(UUID_4);

    public AbstractArrayStorageTest(Storage storage) {
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
        Resume newResume = new Resume(UUID_1);
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
    void saveToOverflowStorage() {
        try {
            for (int i = 3; i < AbstractArrayStorage.STORAGE_LIMIT; i++) {
                storage.save(new Resume());
            }
        } catch (StorageException e){
            Assertions.fail();
        }
        Assertions.assertThrows(StorageException.class, () -> storage.save(new Resume()));
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
    void getAll() {
        Resume[] expected = {storage.get(UUID_1), storage.get(UUID_2), storage.get(UUID_3)};
        Assertions.assertArrayEquals(expected, storage.getAll());

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

    private void assertSize(int size){
        Assertions.assertEquals(size,storage.size());
    }

    private void assertGet(Resume r){
        Assertions.assertEquals(r, storage.get(r.getUuid()));
    }
}