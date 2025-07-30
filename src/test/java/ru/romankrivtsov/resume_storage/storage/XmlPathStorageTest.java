package ru.romankrivtsov.resume_storage.storage;

import ru.romankrivtsov.resume_storage.storage.serializer.XmlStreamSerializer;

class XmlPathStorageTest extends AbstractStorageTest {
    public XmlPathStorageTest() {
        super(new PathStorage(STORAGE_DIR.getAbsolutePath(), new XmlStreamSerializer()));
    }

}