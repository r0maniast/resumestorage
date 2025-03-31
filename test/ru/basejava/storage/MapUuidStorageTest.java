package ru.basejava.storage;

import static org.junit.jupiter.api.Assertions.*;

class MapUuidStorageTest extends AbstractStorageTest{
    protected MapUuidStorageTest(){
        super(new MapUuidStorage());
    }
}