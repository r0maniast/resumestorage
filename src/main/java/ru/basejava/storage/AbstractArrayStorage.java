package ru.basejava.storage;

import ru.basejava.exception.StorageException;
import ru.basejava.model.Resume;

import java.util.Arrays;
import java.util.List;

/**
 * Array based storage for Resumes
 */
public abstract class AbstractArrayStorage extends AbstractStorage {
    protected static final int STORAGE_LIMIT = 10000;

    protected Resume[] storage = new Resume[STORAGE_LIMIT];

    protected int size = 0;

    @Override
    protected boolean isExist(Object searchKey) {
        return (Integer) searchKey >= 0;
    }

    public int size() {
        return size;
    }

    public void clear() {
        Arrays.fill(storage, 0, size, null);
        size = 0;
    }

    public void doUpdate(Resume r, Object index) {
        storage[(Integer) index] = r;
    }

    public void doSave(Resume r, Object index) {
        if (size >= STORAGE_LIMIT) {
            throw new StorageException("Storage overflow", r.getUuid());
        } else {
            insertElement(r, (Integer) index);
            size++;
        }
    }

    public Resume doGet(Object index) {
        return storage[(Integer) index];
    }

    public void doDelete(Object index) {
        fillDeletedElement((Integer) index);
        storage[size - 1] = null;
        size--;
    }

    public List<Resume> doCopyAll() {
        return Arrays.asList(Arrays.copyOf(storage, size));

    }

    protected abstract void insertElement(Resume r, int index);

    protected abstract Integer getSearchKey(String uuid);

    protected abstract void fillDeletedElement(int index);
}