package ru.romankrivtsov.resume_storage.storage;

import ru.romankrivtsov.resume_storage.storage.serializer.ObjectStreamSerializer;

class ObjectFileStorageTest extends AbstractStorageTest{
    public ObjectFileStorageTest(){
        super(new FileStorage(STORAGE_DIR, new ObjectStreamSerializer()));
    }

}