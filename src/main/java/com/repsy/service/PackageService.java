package com.repsy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repsy.model.PackageEntity;
import com.repsy.repository.PackageRepository;
import com.repsy.storage.StorageStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class PackageService {

    private final StorageStrategy storageStrategy;
    private final PackageRepository packageRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public PackageService(StorageStrategy storageStrategy,
                          PackageRepository packageRepository,
                          ObjectMapper objectMapper) {
        this.storageStrategy = storageStrategy;
        this.packageRepository = packageRepository;
        this.objectMapper = objectMapper;
    }

    public void handleUpload(String name, String version, MultipartFile repFile, MultipartFile metaFile) {
        try {
            String basePath = name + "/" + version + "/";
            String repFileName = repFile.getOriginalFilename();
            String metaFileName = metaFile.getOriginalFilename();

            storageStrategy.write(basePath + repFileName, repFile.getInputStream());
            storageStrategy.write(basePath + metaFileName, metaFile.getInputStream());

            JsonNode metaJson = objectMapper.readTree(metaFile.getInputStream());

            PackageEntity entity = new PackageEntity();
            entity.setName(metaJson.get("name").asText());
            entity.setVersion(metaJson.get("version").asText());
            entity.setAuthor(metaJson.get("author").asText());
            entity.setDependenciesJson(metaJson.get("dependencies").toString());
            entity.setRepFileName(repFileName);
            entity.setMetaFileName(metaFileName);

            packageRepository.save(entity);

        } catch (IOException e) {
            throw new RuntimeException("Upload failed", e);
        }
    }

    public InputStream downloadFile(String name, String version, String fileName) {
        String path = name + "/" + version + "/" + fileName;
        return storageStrategy.read(path);
    }

    public List<PackageEntity> getAllPackages() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllPackages'");
    }

    public PackageEntity addPackage(PackageEntity packageEntity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addPackage'");
    }
}
