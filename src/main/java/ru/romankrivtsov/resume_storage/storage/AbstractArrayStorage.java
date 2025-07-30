package ru.romankrivtsov.resume_storage.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.romankrivtsov.resume_storage.exception.StorageException;
import ru.romankrivtsov.resume_storage.model.Resume;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractArrayStorage extends AbstractStorage<Integer> {
    private static final Logger log = LoggerFactory.getLogger(AbstractArrayStorage.class);

    protected static final int STORAGE_LIMIT = 10000;
    protected Resume[] storage = new Resume[STORAGE_LIMIT];
    protected int size = 0;

    @Override
    public int size() {
        log.debug("Retrieving storage size: {}", size);
        return size;
    }

    @Override
    public void clear() {
        log.info("Clearing storage ({} items)", size);
        Arrays.fill(storage, 0, size, null);
        size = 0;
        log.info("Storage cleared; new size = 0");
    }

    @Override
    protected boolean isExist(Integer searchKey) {
        boolean exists = searchKey >= 0;
        log.debug("Checking existence at index {}: {}", searchKey, exists);
        return exists;
    }

    @Override
    protected void doUpdate(Resume r, Integer index) {
        log.info("Updating resume at index {}: {}", index, r.getUuid());
        storage[index] = r;
    }

    @Override
    protected void doSave(Resume r, Integer index) {
        log.info("Saving resume at index {}: {}", index, r.getUuid());
        if (size >= STORAGE_LIMIT) {
            log.error("Storage overflow when saving resume {} (limit = {})", r.getUuid(), STORAGE_LIMIT);
            throw new StorageException("Storage overflow", r.getUuid());
        }
        insertElement(r, index);
        size++;
        log.debug("Resume saved; new size = {}", size);
    }

    @Override
    protected Resume doGet(Integer index) {
        log.debug("Retrieving resume at index {}: {}", index, storage[index] != null ? storage[index].getUuid() : "null");
        return storage[index];
    }

    @Override
    protected void doDelete(Integer index) {
        if (storage[index] != null) {
            log.info("Deleting resume at index {}: {}", index, storage[index].getUuid());
        } else {
            log.warn("Attempt to delete at empty index {}", index);
        }
        fillDeletedElement(index);
        storage[size - 1] = null;
        size--;
        log.debug("Resume deleted; new size = {}", size);
    }

    @Override
    protected List<Resume> doCopyAll() {
        log.debug("Copying all resumes (count = {})", size);
        return Arrays.asList(Arrays.copyOf(storage, size));
    }

    protected abstract void insertElement(Resume r, int index);

    @Override
    protected abstract Integer getSearchKey(String uuid);

    protected abstract void fillDeletedElement(int index);
}
