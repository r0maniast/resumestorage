package ru.romankrivtsov.resume_storage.storage.serializer;

import ru.romankrivtsov.resume_storage.model.Resume;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StreamSerializer {
    void doWrite(Resume r, OutputStream os) throws IOException;

    Resume doRead(InputStream is) throws IOException;

}
