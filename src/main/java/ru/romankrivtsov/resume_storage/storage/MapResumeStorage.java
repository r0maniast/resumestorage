package ru.romankrivtsov.resume_storage.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.romankrivtsov.resume_storage.model.Resume;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapResumeStorage extends AbstractStorage<Resume> {
    private static final Logger log = LoggerFactory.getLogger(MapResumeStorage.class);

    protected Map<String, Resume> storage = new HashMap<>();

    @Override
    public int size() {
        log.debug("Getting storage size: {}", storage.size());
        return storage.size();
    }

    @Override
    public void clear() {
        log.info("Clearing all resumes from storage");
        storage.clear();
    }

    @Override
    protected Resume getSearchKey(String uuid) {
        log.debug("Getting search key for uuid={}", uuid);
        return storage.get(uuid);
    }

    @Override
    protected boolean isExist(Resume resume) {
        boolean exists = resume != null;
        log.debug("Checking existence for resume: {} -> {}", resume, exists);
        return exists;
    }

    @Override
    protected Resume doGet(Resume resume) {
        log.info("Getting resume: {}", resume.getUuid());
        return resume;
    }

    @Override
    protected void doUpdate(Resume r, Resume resume) {
        log.info("Updating resume: {}", r.getUuid());
        storage.put(r.getUuid(), r);
    }

    @Override
    protected void doSave(Resume r, Resume resume) {
        log.info("Saving new resume: {}", r.getUuid());
        storage.put(r.getUuid(), r);
    }

    @Override
    protected void doDelete(Resume resume) {
        log.info("Deleting resume: {}", resume.getUuid());
        storage.remove(resume.getUuid());
    }

    @Override
    protected List<Resume> doCopyAll() {
        log.debug("Copying all resumes to list");
        return new ArrayList<>(storage.values());
    }
}
