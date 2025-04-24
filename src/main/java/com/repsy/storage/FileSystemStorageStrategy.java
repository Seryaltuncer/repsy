package com.repsy.storage;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@Component
public class FileSystemStorageStrategy implements StorageStrategy {

    private final Path root = Paths.get("storage");

    public FileSystemStorageStrategy() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }

    @Override
    public void write(String path, InputStream content) {
        try {
            Path fullPath = root.resolve(path).normalize();
            Files.createDirectories(fullPath.getParent());
            Files.copy(content, fullPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + path, e);
        }
    }

    @Override
    public InputStream read(String path) {
        try {
            Path fullPath = root.resolve(path).normalize();
            if (!Files.exists(fullPath)) {
                throw new RuntimeException("File not found: " + path);
            }
            return Files.newInputStream(fullPath, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
    }
}