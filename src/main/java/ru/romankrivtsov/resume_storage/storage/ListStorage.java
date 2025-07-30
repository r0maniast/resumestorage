package ru.romankrivtsov.resume_storage.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.romankrivtsov.resume_storage.model.Resume;

import java.util.ArrayList;
import java.util.List;

public class ListStorage extends AbstractStorage<Integer> {
    private static final Logger log = LoggerFactory.getLogger(ListStorage.class);

    protected List<Resume> storage = new ArrayList<>();

    @Override
    public int size() {
        int currentSize = storage.size();
        log.debug("Retrieving storage size: {}", currentSize);
        return currentSize;
    }

    @Override
    public void clear() {
        log.info("Clearing storage, current size: {}", storage.size());
        storage.clear();
        log.info("Storage cleared; new size: 0");
    }

    @Override
    protected Integer getSearchKey(String uuid) {
        log.debug("Searching for resume with uuid={}", uuid);
        for (int i = 0; i < storage.size(); i++) {
            if (uuid.equals(storage.get(i).getUuid())) {
                log.debug("Found resume {} at index {}", uuid, i);
                return i;
            }
        }
        log.debug("Resume {} not found in storage", uuid);
        return -1;
    }

    @Override
    protected boolean isExist(Integer searchKey) {
        boolean exists = searchKey >= 0;
        log.debug("Checking existence for index {}: {}", searchKey, exists);
        return exists;
    }

    @Override
    protected void doUpdate(Resume r, Integer searchKey) {
        log.info("Updating resume at index {}: {}", searchKey, r.getUuid());
        storage.set(searchKey, r);
        log.debug("Resume {} updated at index {}", r.getUuid(), searchKey);
    }

    @Override
    protected void doSave(Resume r, Integer searchKey) {
        log.info("Saving resume {}", r.getUuid());
        storage.add(r);
        log.debug("Resume {} added; new size: {}", r.getUuid(), storage.size());
    }

    @Override
    protected Resume doGet(Integer searchKey) {
        log.debug("Retrieving resume at index {}", searchKey);
        Resume r = storage.get(searchKey);
        log.info("Retrieved resume {} at index {}", r.getUuid(), searchKey);
        return r;
    }

    @Override
    protected void doDelete(Integer searchKey) {
        log.info("Deleting resume at index {}: {}", searchKey, storage.get(searchKey).getUuid());
        storage.remove((int) searchKey);
        log.debug("Resume removed; new size: {}", storage.size());
    }

    @Override
    protected List<Resume> doCopyAll() {
        log.debug("Copying all resumes; count: {}", storage.size());
        List<Resume> copy = new ArrayList<>(storage);
        log.info("Copied {} resumes", copy.size());
        return copy;
    }
}
