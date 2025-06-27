package ru.webapp.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webapp.model.Resume;

public class ArrayStorage extends AbstractArrayStorage {
    private static final Logger log = LoggerFactory.getLogger(ArrayStorage.class);

    @Override
    protected void insertElement(Resume r, int index) {
        log.debug("Inserting resume {} at index {}", r.getUuid(), index);
        storage[size] = r;
        log.info("Resume {} inserted at index {}", r.getUuid(), index);
    }

    @Override
    protected Integer getSearchKey(String uuid) {
        log.debug("Searching for resume with uuid={}", uuid);
        for (int i = 0; i < size; i++) {
            if (uuid.equals(storage[i].getUuid())) {
                log.debug("Found resume {} at index {}", uuid, i);
                return i;
            }
        }
        log.debug("Resume {} not found in storage", uuid);
        return -1;
    }

    @Override
    protected void fillDeletedElement(int index) {
        log.debug("Filling deleted element at index {} with last element index {}", index, size - 1);
        storage[index] = storage[size - 1];
        log.info("Element at index {} replaced with element from index {}", index, size - 1);
    }
}
