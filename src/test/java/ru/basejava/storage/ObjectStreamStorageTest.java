package ru.basejava.storage;

import ru.basejava.storage.serializer.ObjectStreamSerializer;

class ObjectStreamStorageTest extends AbstractStorageTest{
    public ObjectStreamStorageTest(){
        super(new FileStorage(STORAGE_DIR, new ObjectStreamSerializer()));
    }

}