package uz.jl.library.service;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


public class Image implements Runnable {
    private String taskName;


    public Image(String str) {

        taskName = str;
    }

    @SneakyThrows
    public void run() {

        String path1 = "";

        String name = UUID.randomUUID() + ".jpg";

        File file = new File("/home/shohimardon/Desktop/images/" + name);
        try {
            file.mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            e.getMessage();
        }
        String url = taskName;
        Path path = Paths.get("/home/shohimardon/Desktop/images/" + name);
        path1 = String.valueOf(path);
        try (InputStream inputStream = new URL(url).openStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException();
        }


    }


}

