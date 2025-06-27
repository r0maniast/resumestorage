package ru.webapp.storage;

import ru.webapp.Config;

public class SqlStorageTest extends AbstractStorageTest {
    public SqlStorageTest(){
        super(Config.get().getStorage());
    }
}