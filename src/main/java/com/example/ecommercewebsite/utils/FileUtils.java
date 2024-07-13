package com.example.ecommercewebsite.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
public class FileUtils {

    private final Executor executor;
    private static final String JAVA_TO_TMPDIR = "java.io.tmpdir";
    public static File convertMultipartFileToFile(MultipartFile multipartFile){
        if(Objects.isNull(multipartFile)) {
            return null;
        }

        File file = new File(System.getProperty(JAVA_TO_TMPDIR) + "tmp_" + UUID.randomUUID() + "_" + multipartFile.getName() + ".jpg");
        try (OutputStream os = Files.newOutputStream(file.toPath())) {
            os.write(multipartFile.getBytes());
        } catch (IOException e) {
            //do no thing
        }
        return file;
    }

    public void deleteMultiFileAsynchronous(File... files) {
        Arrays.stream(files).forEach(this::deleteMultiFileAsynchronous);
    }

    public void deleteFileAsynchronous(final File file){
        if(Objects.nonNull(file)) {
            CompletableFuture.runAsync(() -> {
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (Exception e) {

                }
            }, executor);
        }
    }
}
