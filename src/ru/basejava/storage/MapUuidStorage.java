package ru.basejava.storage;

import ru.basejava.model.Resume;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUuidStorage extends AbstractStorage {

    protected Map<String, Resume> storage = new HashMap<>();

    @Override
    protected boolean isExist(Object searchKey) {
        return !"null".equals(searchKey.toString());
    }

    @Override
    protected String getSearchKey(String uuid) {
        if (storage.containsKey(uuid)) {
            return uuid;
        }
        return "null";
        /*for (Map.Entry<String, String> pair : storage.entrySet()) {
            if (pair.getKey().equals(uuid)) {
                return pair.getKey();
            }
        }
        return null;*/
    }

    @Override
    protected void doSave(Resume r, Object searchKey) {
        storage.put(r.getUuid(), r);
    }

    @Override
    protected void doDelete(Object searchKey) {
        storage.remove((String) searchKey);
    }

    @Override
    protected void doUpdate(Resume r, Object searchKey) {
        storage.replace((String) searchKey, r);
    }

    @Override
    protected Resume doGet(Object searchKey) {
        return storage.get((String) searchKey);
    }

    @Override
    public void clear() {
        storage.clear();
    }

    @Override
    public List<Resume> getAllSorted() {
        List<Resume> resumeList = new ArrayList<>();
        for(Map.Entry<String,Resume> pair : storage.entrySet()){
            resumeList.add(pair.getValue());
        }
        resumeList.sort(Resume::compareTo);
        return resumeList;
    }

    @Override
    public int size() {
        return storage.size();
    }
}
