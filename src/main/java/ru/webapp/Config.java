package ru.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webapp.storage.SqlStorage;
import ru.webapp.storage.Storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class.getName());
    protected final static String PROPS = "/resumes.properties";
    private static final Config INSTANCE = new Config();

    private final File storageDir;
    private final Storage storage;

    public static Config get() {
        log.debug("Retrieving Config singleton instance");
        return INSTANCE;
    }

    private Config() {
        log.info("Loading configuration from classpath: {}", PROPS);
        Properties props = new Properties();
        try (InputStream is = Config.class.getResourceAsStream(PROPS)) {
            if(is == null){
                throw new IllegalStateException(PROPS + " not found in classpath");
            }
            props.load(is);
        } catch (IOException e) {
            log.error("Failed to load configuration from {}", PROPS, e);
            throw new IllegalStateException("Invalid config file: " + PROPS, e);
        }

        String dirPath = getEnvOrDefault("STORAGE_DIR", props.getProperty("storage.dir"));
        storageDir = new File(dirPath);
        log.info("Configured storage directory: {}", storageDir.getAbsolutePath());

        String dbUrl = getEnvOrDefault("DATABASE_URL", props.getProperty("db.url"));
        String dbUser = getEnvOrDefault("DATABASE_USER", props.getProperty("db.user"));
        String dbPass = getEnvOrDefault("DATABASE_PASSWORD", props.getProperty("db.password"));
        log.info("Initializing SqlStorage with URL={} and user={}", dbUrl, dbUser);
        
        storage = new SqlStorage(dbUrl, dbUser, dbPass);
        log.info("SqlStorage initialized successfully");
    }

    public File getStorageDir() {
        log.debug("getStorageDir() called, returning {}", storageDir.getAbsolutePath());
        return storageDir;
    }

    public Storage getStorage() {
        log.debug("getStorage() called, returning storage implementation: {}", storage.getClass().getSimpleName());
        return storage;
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String env = System.getenv(key);
        return (env != null && !env.isBlank()) ? env : defaultValue;
    }
}
