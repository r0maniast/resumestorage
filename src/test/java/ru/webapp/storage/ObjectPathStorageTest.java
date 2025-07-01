package ru.webapp.storage;

import ru.webapp.storage.serializer.ObjectStreamSerializer;

class ObjectPathStorageTest extends AbstractStorageTest {
    public ObjectPathStorageTest() {
        super(new PathStorage(STORAGE_DIR.getAbsolutePath(), new ObjectStreamSerializer()));
    }

}