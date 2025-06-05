package ru.webapp.storage;

import ru.webapp.Config;

class SqlStorageTest extends AbstractStorageTest {
    public SqlStorageTest(){
        super(Config.get().getStorage());
    }
}