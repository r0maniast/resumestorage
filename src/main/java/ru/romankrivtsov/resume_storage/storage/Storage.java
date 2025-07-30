package ru.romankrivtsov.resume_storage.storage;

import ru.romankrivtsov.resume_storage.model.Resume;

import java.util.List;

/**
 * Array based storage for Resumes
 */
public interface Storage {

    int size();

    void clear();

    void update(Resume r);

    void save(Resume r);

    Resume get(String uuid);

    void delete(String uuid);

    List<Resume> getAllSorted();
}