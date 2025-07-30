package ru.romankrivtsov.resume_storage.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.romankrivtsov.resume_storage.exception.StorageException;
import ru.romankrivtsov.resume_storage.model.Resume;
import ru.romankrivtsov.resume_storage.storage.serializer.StreamSerializer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileStorage extends AbstractStorage<File> {
    private static final Logger log = LoggerFactory.getLogger(FileStorage.class);

    private final File directory;
    private final StreamSerializer streamSerializer;

    protected FileStorage(File directory, StreamSerializer streamSerializer) {
        Objects.requireNonNull(directory, "directory must not be null");
        log.info("Initializing FileStorage with directory: {}", directory.getAbsolutePath());

        this.streamSerializer = streamSerializer;
        if (!directory.isDirectory()) {
            log.error("Provided path is not a directory: {}", directory.getAbsolutePath());
            throw new IllegalArgumentException(directory.getAbsolutePath() + " is not directory");
        }
        if (!directory.canRead() || !directory.canWrite()) {
            log.error("No read/write access to directory: {}", directory.getAbsolutePath());
            throw new IllegalArgumentException(directory.getAbsolutePath() + " is not readable/writable");
        }
        this.directory = directory;
        log.info("FileStorage initialized successfully");
    }

    @Override
    public int size() {
        String[] files = directory.list();
        if (files == null) {
            log.error("Failed to list files in directory: {}", directory.getAbsolutePath());
            throw new StorageException("Directory read error");
        }
        log.debug("Directory contains {} files", files.length);
        return files.length;
    }

    @Override
    public void clear() {
        log.info("Clearing storage directory: {}", directory.getAbsolutePath());
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                doDelete(file);
            }
        }
        log.info("Storage directory cleared");
    }

    @Override
    protected File getSearchKey(String uuid) {
        File file = new File(directory, uuid);
        log.debug("Computed search key for uuid {}: {}", uuid, file.getAbsolutePath());
        return file;
    }

    @Override
    protected boolean isExist(File file) {
        boolean exists = file.exists();
        log.debug("File {} exists: {}", file.getName(), exists);
        return exists;
    }

    @Override
    protected void doUpdate(Resume r, File file) {
        log.info("Updating resume {} in file {}", r.getUuid(), file.getName());
        try {
            streamSerializer.doWrite(r, new BufferedOutputStream(new FileOutputStream(file)));
            log.debug("Resume {} written to file {} successfully", r.getUuid(), file.getName());
        } catch (IOException e) {
            log.error("Error writing resume {} to file {}", r.getUuid(), file.getName(), e);
            throw new StorageException("File write error", file.getName(), e);
        }
    }

    @Override
    protected void doSave(Resume r, File file) {
        log.info("Saving new resume {} to file {}", r.getUuid(), file.getName());
        try {
            if (!file.createNewFile()) {
                log.warn("File {} already exists", file.getAbsolutePath());
            }
            log.debug("File {} created", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Could not create file {}", file.getAbsolutePath(), e);
            throw new StorageException("Couldn't create file " + file.getAbsolutePath(), file.getName(), e);
        }
        doUpdate(r, file);
    }

    @Override
    protected Resume doGet(File file) {
        log.info("Reading resume from file {}", file.getName());
        try {
            Resume r = streamSerializer.doRead(new BufferedInputStream(new FileInputStream(file)));
            log.debug("Resume {} read from file {} successfully", r.getUuid(), file.getName());
            return r;
        } catch (IOException e) {
            log.error("Error reading resume from file {}", file.getName(), e);
            throw new StorageException("File read error", file.getName(), e);
        }
    }

    @Override
    protected void doDelete(File file) {
        log.info("Deleting file {}", file.getName());
        if (!file.delete()) {
            log.error("Error deleting file {}", file.getName());
            throw new StorageException("File delete error", file.getName());
        }
        log.debug("File {} deleted successfully", file.getName());
    }

    @Override
    protected List<Resume> doCopyAll() {
        log.info("Copying all resumes from directory {}", directory.getAbsolutePath());
        File[] files = directory.listFiles();
        if (files == null) {
            log.error("Failed to list files in directory: {}", directory.getAbsolutePath());
            throw new StorageException("Directory read error");
        }
        List<Resume> list = new ArrayList<>(files.length);
        for (File file : files) {
            list.add(doGet(file));
        }
        log.debug("Copied {} resumes from storage", list.size());
        return list;
    }
}