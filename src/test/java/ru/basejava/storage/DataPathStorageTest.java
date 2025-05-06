package ru.basejava.storage;

import ru.basejava.storage.serializer.DataStreamSerializer;

class DataPathStorageTest extends AbstractStorageTest {
    public DataPathStorageTest() {
        super(new PathStorage(STORAGE_DIR.getAbsolutePath(), new DataStreamSerializer()));
    }

}