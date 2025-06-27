package ru.webapp.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webapp.model.Resume;

import java.util.Arrays;
import java.util.Comparator;

public class SortedArrayStorage extends AbstractArrayStorage {
    private static final Logger log = LoggerFactory.getLogger(SortedArrayStorage.class);
    private static final Comparator<Resume> RESUME_COMPARATOR = Comparator.comparing(Resume::getUuid);

    @Override
    protected void fillDeletedElement(int index) {
        log.debug("Filling deleted element at index {}", index);
        int numMoved = size - (index + 1);
        if (numMoved > 0) {
            System.arraycopy(storage, index + 1, storage, index, numMoved);
            log.info("Shifted {} elements left from index {}", numMoved, index + 1);
        }
    }

    @Override
    protected void insertElement(Resume r, int index) {
        index = -index - 1;
        log.debug("Inserting resume {} at position {}", r.getUuid(), index);
        System.arraycopy(storage, index, storage, index + 1, size - index);
        storage[index] = r;
        log.info("Inserted resume {} at index {}", r.getUuid(), index);
    }

    @Override
    protected Integer getSearchKey(String uuid) {
        log.debug("Searching for resume with uuid={} using binary search", uuid);
        Resume searchKey = new Resume(uuid, "dummy");
        int index = Arrays.binarySearch(storage, 0, size, searchKey, RESUME_COMPARATOR);
        if (index >= 0) {
            log.debug("Resume {} found at index {}", uuid, index);
        } else {
            log.debug("Resume {} not found, insertion point {}", uuid, -index - 1);
        }
        return index;
    }
}
