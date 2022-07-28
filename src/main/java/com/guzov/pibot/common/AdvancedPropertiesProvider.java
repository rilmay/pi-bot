package com.guzov.pibot.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.UnaryOperator;

public class AdvancedPropertiesProvider {
    private static final Lock lock = new ReentrantLock();
    private static AdvancedPropertiesProvider instance;
    private final Map<String, Properties> propertiesCache = new HashMap<>();

    public static AdvancedPropertiesProvider getInstance() {
        lock.lock();
        try {
            if (instance == null) {
                instance = new AdvancedPropertiesProvider();
            }

        } finally {
            lock.unlock();
        }
        return instance;
    }

    public Properties readProperties(String propertiesFileName, UnaryOperator<String> externalPropertiesResolver) {
        if (propertiesFileName == null || propertiesFileName.isBlank()) {
            throw new IllegalArgumentException("Please check properties file name");
        }
        if (propertiesCache.containsKey(propertiesFileName)) {
            return propertiesCache.get(propertiesFileName);
        } else {
            Properties prop = new ExternalPropertiesWrapper(externalPropertiesResolver);
            try (InputStream input =
                         AdvancedPropertiesProvider.class.getClassLoader().getResourceAsStream(propertiesFileName)) {
                if (input == null) {
                    throw new IllegalArgumentException("Properties file was not found");
                }
                prop.load(input);
            } catch (IOException e) {
                throw new IllegalStateException("Error while loading properties " + propertiesFileName);
            }
            propertiesCache.put(propertiesFileName, prop);
            return prop;
        }
    }
}
