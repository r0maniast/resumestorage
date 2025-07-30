package ru.romankrivtsov.resume_storage.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.romankrivtsov.resume_storage.model.Resume;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUuidStorage extends AbstractStorage<String> {
    private static final Logger log = LoggerFactory.getLogger(MapUuidStorage.class);

    protected Map<String, Resume> storage = new HashMap<>();

    @Override
    public int size() {
        log.debug("Getting size of storage: {}", storage.size());
        return storage.size();
    }

    @Override
    public void clear() {
        log.info("Clearing all resumes from storage");
        storage.clear();
    }

    @Override
    protected String getSearchKey(String uuid) {
        log.debug("Returning search key for uuid={}", uuid);
        return uuid;
    }

    @Override
    protected boolean isExist(String uuid) {
        boolean exists = storage.containsKey(uuid);
        log.debug("Checking existence of resume with uuid={}: {}", uuid, exists);
        return exists;
    }

    @Override
    protected Resume doGet(String uuid) {
        log.info("Retrieving resume with uuid={}", uuid);
        return storage.get(uuid);
    }

    @Override
    protected void doUpdate(Resume r, String uuid) {
        log.info("Updating resume with uuid={}", uuid);
        storage.replace(uuid, r);
    }

    @Override
    protected void doSave(Resume r, String uuid) {
        log.info("Saving new resume with uuid={}", uuid);
        storage.put(uuid, r);
    }

    @Override
    protected void doDelete(String uuid) {
        log.info("Deleting resume with uuid={}", uuid);
        storage.remove(uuid);
    }

    @Override
    protected List<Resume> doCopyAll() {
        log.debug("Copying all resumes to a new list");
        return new ArrayList<>(storage.values());
    }
}
