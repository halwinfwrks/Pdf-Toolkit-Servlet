package com.dev.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnvConfig {
    private static final String TAG = EnvConfig.class.getSimpleName();
    private static final Logger logger = LogManager.getLogger(TAG);
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = EnvConfig.class.getClassLoader().getResourceAsStream("env.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                logger.error("{}: env.properties file not found in classpath", TAG);
            }
        } catch (IOException e) {
            logger.error("{}: Error reading env.properties file", TAG, e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

}
