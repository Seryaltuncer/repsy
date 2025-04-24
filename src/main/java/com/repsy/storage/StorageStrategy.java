package com.repsy.storage;

import java.io.InputStream;

public interface StorageStrategy {
    void write(String path, InputStream content);
    InputStream read(String path);
}