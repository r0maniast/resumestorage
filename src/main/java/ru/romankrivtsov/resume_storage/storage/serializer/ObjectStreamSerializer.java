package ru.romankrivtsov.resume_storage.storage.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.romankrivtsov.resume_storage.exception.StorageException;
import ru.romankrivtsov.resume_storage.model.Resume;

import java.io.*;

public class ObjectStreamSerializer implements StreamSerializer {
    private static final Logger log = LoggerFactory.getLogger(ObjectStreamSerializer.class);

    @Override
    public void doWrite(Resume r, OutputStream os) throws IOException {
        log.debug("Starting object stream write of resume {}", r.getUuid());
        try (ObjectOutputStream oos = new ObjectOutputStream(os)) {
            oos.writeObject(r);
            log.debug("Finished object stream write of resume {}", r.getUuid());
        } catch (IOException e) {
            log.error("Error writing resume {} to object stream", r.getUuid(), e);
            throw e;
        }
    }

    @Override
    public Resume doRead(InputStream is) throws IOException {
        log.debug("Starting object stream read of resume");
        try (ObjectInputStream ois = new ObjectInputStream(is)) {
            Object obj = ois.readObject();
            if (obj instanceof Resume resume) {
                log.debug("Finished object stream read of resume {}", resume.getUuid());
                return resume;
            } else {
                String msg = "Unexpected object type read: " + obj.getClass();
                log.error(msg);
                throw new StorageException(msg, null, null);
            }
        } catch (ClassNotFoundException e) {
            String msg = "Class not found during object stream read";
            log.error(msg, e);
            throw new StorageException(msg, null, e);
        } catch (IOException e) {
            log.error("IO error reading resume from object stream", e);
            throw e;
        }
    }
}
