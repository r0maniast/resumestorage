package ru.webapp.storage;

import ru.webapp.storage.serializer.JsonStreamSerializer;

class JsonPathStorageTest extends AbstractStorageTest {
    public JsonPathStorageTest() {
        super(new PathStorage(STORAGE_DIR.getAbsolutePath(), new JsonStreamSerializer()));
    }

}