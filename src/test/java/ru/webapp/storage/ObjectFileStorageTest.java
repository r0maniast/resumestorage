package ru.webapp.storage;

import ru.webapp.storage.serializer.ObjectStreamSerializer;

class ObjectFileStorageTest extends AbstractStorageTest{
    public ObjectFileStorageTest(){
        super(new FileStorage(STORAGE_DIR, new ObjectStreamSerializer()));
    }

}