package uz.jl.library.servlet;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Property {
    static Properties properties = new Properties();

    static {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("src/main/resources/application.properties");
            properties.load(fileReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String key) {
        try {
            return properties.getProperty(key, key);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}