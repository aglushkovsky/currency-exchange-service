package io.github.aglushkovsky.util;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtils {
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try {
            PROPERTIES.load(PropertiesUtils.class.getClassLoader()
                    .getResourceAsStream("application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

    private PropertiesUtils() {
    }
}
