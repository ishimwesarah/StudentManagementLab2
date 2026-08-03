package service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads application-wide configuration once, from a properties file
 * bundled with the application (src/main/resources/application.properties).
 *
 * This is the "administrator decides, not each individual request"
 * pattern discussed for thread pool sizing - whoever deploys this
 * application can change export.threadPoolSize by editing a plain
 * text file, with no code changes or recompilation needed.
 */
public class AppConfig {

    private static final String CONFIG_FILE = "application.properties";
    private static final int DEFAULT_THREAD_POOL_SIZE = 4;

    private final Properties properties;

    public AppConfig() {
        this.properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            } else {
                System.out.println("(No " + CONFIG_FILE + " found - using default configuration.)");
            }
        } catch (IOException e) {
            System.out.println("(Could not load " + CONFIG_FILE + ": " + e.getMessage() + " - using default configuration.)");
        }
    }

    /**
     * @return the configured export thread pool size, or a safe
     *         default if the config file is missing or the value
     *         is invalid
     */
    public int getExportThreadPoolSize() {
        String value = properties.getProperty("export.threadPoolSize");
        if (value == null) {
            return DEFAULT_THREAD_POOL_SIZE;
        }

        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed < 2 || parsed > 8) {
                System.out.println("(Configured export.threadPoolSize=" + parsed
                        + " is outside the allowed range 2-8 - using default of " + DEFAULT_THREAD_POOL_SIZE + ".)");
                return DEFAULT_THREAD_POOL_SIZE;
            }
            return parsed;
        } catch (NumberFormatException e) {
            System.out.println("(Configured export.threadPoolSize is not a valid number - using default of "
                    + DEFAULT_THREAD_POOL_SIZE + ".)");
            return DEFAULT_THREAD_POOL_SIZE;
        }
    }
}