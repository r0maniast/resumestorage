package ru.romankrivtsov.resume_storage.storage;

import ru.romankrivtsov.resume_storage.Config;

class SqlStorageTest extends AbstractStorageTest {
    public SqlStorageTest(){
        super(Config.get().getStorage());
    }
}