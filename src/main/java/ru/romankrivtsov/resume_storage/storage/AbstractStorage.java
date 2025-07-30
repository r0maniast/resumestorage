package ru.romankrivtsov.resume_storage.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.romankrivtsov.resume_storage.exception.ExistStorageException;
import ru.romankrivtsov.resume_storage.model.Resume;
import ru.romankrivtsov.resume_storage.exception.NotExistStorageException;

import java.util.Collections;
import java.util.List;

public abstract class AbstractStorage<SK> implements Storage {
    private static final Logger log = LoggerFactory.getLogger(AbstractStorage.class);

    @Override
    public void update(Resume r) {
        log.info("Updating resume: {}", r.getUuid());
        SK searchKey = getExistedSearchKey(r.getUuid());
        doUpdate(r, searchKey);
        log.info("Resume updated successfully: {}", r.getUuid());
    }

    @Override
    public void save(Resume r) {
        log.info("Saving resume: {}", r.getUuid());
        SK searchKey = getNotExistedSearchKey(r.getUuid());
        doSave(r, searchKey);
        log.info("Resume saved successfully: {}", r.getUuid());
    }

    @Override
    public Resume get(String uuid) {
        log.info("Retrieving resume: {}", uuid);
        SK searchKey = getExistedSearchKey(uuid);
        Resume r = doGet(searchKey);
        log.info("Resume retrieved successfully: {}", uuid);
        return r;
    }

    @Override
    public void delete(String uuid) {
        log.info("Deleting resume: {}", uuid);
        SK searchKey = getExistedSearchKey(uuid);
        doDelete(searchKey);
        log.info("Resume deleted successfully: {}", uuid);
    }

    @Override
    public List<Resume> getAllSorted() {
        log.info("Retrieving all resumes sorted");
        List<Resume> list = doCopyAll();
        Collections.sort(list);
        log.info("Retrieved {} resumes", list.size());
        return list;
    }

    private SK getExistedSearchKey(String uuid) {
        log.debug("Checking existence for uuid={}", uuid);
        SK searchKey = getSearchKey(uuid);
        if (!isExist(searchKey)) {
            log.warn("Resume does not exist: {}", uuid);
            throw new NotExistStorageException(uuid);
        }
        log.debug("Resume exists: {}", uuid);
        return searchKey;
    }

    private SK getNotExistedSearchKey(String uuid) {
        log.debug("Checking non-existence for uuid={}", uuid);
        SK searchKey = getSearchKey(uuid);
        if (isExist(searchKey)) {
            log.warn("Resume already exists: {}", uuid);
            throw new ExistStorageException(uuid);
        }
        log.debug("Resume does not exist (as expected): {}", uuid);
        return searchKey;
    }

    protected abstract SK getSearchKey(String uuid);

    protected abstract boolean isExist(SK searchKey);

    protected abstract Resume doGet(SK searchKey);

    protected abstract void doUpdate(Resume r, SK searchKey);

    protected abstract void doSave(Resume r, SK searchKey);

    protected abstract void doDelete(SK searchKey);

    protected abstract List<Resume> doCopyAll();
}
