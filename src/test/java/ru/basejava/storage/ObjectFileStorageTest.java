package ru.basejava.storage;

import ru.basejava.storage.serializer.ObjectStreamSerializer;

class ObjectFileStorageTest extends AbstractStorageTest{
    public ObjectFileStorageTest(){
        super(new FileStorage(STORAGE_DIR, new ObjectStreamSerializer()));
    }

}