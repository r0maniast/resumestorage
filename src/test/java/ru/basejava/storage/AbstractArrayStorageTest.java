package ru.basejava.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.basejava.exception.StorageException;
import ru.basejava.model.Resume;

abstract class AbstractArrayStorageTest extends AbstractStorageTest {
    public AbstractArrayStorageTest(Storage storage) {
        super(storage);
    }

    @Test
    void saveToOverflowStorage() {
        try {
            for (int i = 3; i < AbstractArrayStorage.STORAGE_LIMIT; i++) {
                storage.save(new Resume("Name" + i));
            }
        } catch (StorageException e) {
            Assertions.fail();
        }
        Assertions.assertThrows(StorageException.class, () -> storage.save(new Resume("Overflow")));
    }
}