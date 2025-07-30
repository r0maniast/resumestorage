package ru.romankrivtsov.resume_storage.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.romankrivtsov.resume_storage.exception.StorageException;
import ru.romankrivtsov.resume_storage.model.Resume;
import ru.romankrivtsov.resume_storage.storage.serializer.StreamSerializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathStorage extends AbstractStorage<Path> {
    private static final Logger log = LoggerFactory.getLogger(PathStorage.class);

    private final Path directory;
    private final StreamSerializer streamSerializer;

    protected PathStorage(String dir, StreamSerializer streamSerializer) {
        directory = Paths.get(dir);
        this.streamSerializer = streamSerializer;
        Objects.requireNonNull(directory, "directory must not be null");
        if (!Files.isDirectory(directory) || !Files.isWritable(directory)) {
            throw new IllegalArgumentException(dir + " is not directory or is not writable");
        }
        log.info("Initialized PathStorage with directory '{}'", directory);
    }

    @Override
    public int size() {
        try (Stream<Path> paths = getFilesList()) {
            int count = (int) paths.count();
            log.debug("Storage size: {}", count);
            return count;
        }
    }

    @Override
    public void clear() {
        try (Stream<Path> paths = getFilesList()) {
            paths.forEach(this::doDelete);
            log.info("Storage cleared");
        }
    }

    @Override
    protected Path getSearchKey(String uuid) {
        Path path = directory.resolve(uuid);
        log.debug("Resolved path for uuid {}: {}", uuid, path);
        return path;
    }

    @Override
    protected boolean isExist(Path path) {
        boolean exists = Files.isRegularFile(path);
        log.debug("Checking existence of file {}: {}", path, exists);
        return exists;
    }

    @Override
    protected void doUpdate(Resume r, Path path) {
        log.info("Updating resume with uuid={} at path={}", r.getUuid(), path);
        try {
            streamSerializer.doWrite(r, new BufferedOutputStream(Files.newOutputStream(path)));
        } catch (IOException e) {
            log.error("Failed to write resume to path={}", path, e);
            throw new StorageException("Couldn't update file " + path.getFileName(), getFileName(path), e);
        }
    }

    @Override
    protected void doSave(Resume r, Path path) {
        log.info("Saving resume with uuid={} to new path={}", r.getUuid(), path);
        try {
            Files.createFile(path);
        } catch (IOException e) {
            log.error("Couldn't create file {}", path, e);
            throw new StorageException("Couldn't create Path " + path, getFileName(path), e);
        }
        doUpdate(r, path);
    }

    @Override
    protected Resume doGet(Path path) {
        log.info("Reading resume from path={}", path);
        try {
            return streamSerializer.doRead(new BufferedInputStream(Files.newInputStream(path)));
        } catch (IOException e) {
            log.error("Failed to read resume from path={}", path, e);
            throw new StorageException("Path read error", getFileName(path), e);
        }
    }

    @Override
    protected void doDelete(Path path) {
        log.info("Deleting resume at path={}", path);
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.error("Failed to delete file {}", path, e);
            throw new StorageException("Path delete error", getFileName(path), e);
        }
    }

    @Override
    protected List<Resume> doCopyAll() {
        log.info("Copying all resumes from directory {}", directory);
        try (Stream<Path> paths = getFilesList()) {
            return paths.map(this::doGet).collect(Collectors.toList());
        }
    }

    private String getFileName(Path path) {
        return path.getFileName().toString();
    }

    private Stream<Path> getFilesList() {
        try {
            return Files.list(directory);
        } catch (IOException e) {
            log.error("Failed to list files in directory {}", directory, e);
            throw new StorageException("Directory read error", e);
        }
    }
}
