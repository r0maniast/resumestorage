package ru.basejava.storage;

import ru.basejava.storage.serializer.JsonStreamSerializer;

class JsonPathStorageTest extends AbstractStorageTest {
    public JsonPathStorageTest() {
        super(new PathStorage(STORAGE_DIR.getAbsolutePath(), new JsonStreamSerializer()));
    }

}