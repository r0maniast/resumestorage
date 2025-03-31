package ru.javawebinar.basejava.storage;

import ru.javawebinar.basejava.model.Resume;

import java.util.ArrayList;
import java.util.List;

public class MapUuidStorage extends AbstractStorage{
    @Override
    protected Object getSearchKey(String uuid) {
        return null;
    }

    @Override
    protected void doSave(Resume r, Object searchKey) {

    }

    @Override
    protected void doDelete(Object searchKey) {

    }

    @Override
    protected void doUpdate(Resume r, Object searchKey) {

    }

    @Override
    protected Resume doGet(Object searchKey) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public List<Resume> getAllSorted() {
        return new ArrayList<>();
    }

    @Override
    public int size() {
        return 0;
    }
}
