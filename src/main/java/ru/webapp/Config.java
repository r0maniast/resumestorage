package ru.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webapp.storage.SqlStorage;
import ru.webapp.storage.Storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class.getName());
    protected final static File PROPS = new File(getHomeDir(), "src/main/resources/resumes.properties");
    private static final Config INSTANCE = new Config();

    private final File storageDir;
    private final Storage storage;

    public static Config get() {
        log.debug("Retrieving Config singleton instance");
        return INSTANCE;
    }

    private Config() {
        log.info("Initializing Config from properties file: {}", PROPS.getAbsolutePath());
        try (InputStream is = new FileInputStream(PROPS)) {
            Properties props = new Properties();
            props.load(is);

            String dirPath = props.getProperty("storage.dir");
            storageDir = new File(dirPath);
            log.info("Configured storage directory: {}", storageDir.getAbsolutePath());

            String dbUrl = props.getProperty("db.url");
            String dbUser = props.getProperty("db.user");
            log.info("Initializing SqlStorage with URL={} and user={}", dbUrl, dbUser);
            storage = new SqlStorage(dbUrl, dbUser, props.getProperty("db.password"));
            log.info("SqlStorage initialized successfully");
        } catch (IOException e) {
            log.error("Failed to load configuration from {}", PROPS.getAbsolutePath(), e);
            throw new IllegalStateException("Invalid config file: " + PROPS.getAbsolutePath(), e);
        }
    }

    public File getStorageDir() {
        log.debug("getStorageDir() called, returning {}", storageDir.getAbsolutePath());
        return storageDir;
    }

    public Storage getStorage() {
        log.debug("getStorage() called, returning storage implementation: {}", storage.getClass().getSimpleName());
        return storage;
    }

    private static File getHomeDir() {
        String prop = System.getProperty("homeDir");
        File homeDir = new File(prop == null ? "." : prop);
        log.debug("Determined home directory: {}", homeDir.getAbsolutePath());
        if (!homeDir.isDirectory()) {
            log.error("Home directory {} is not a directory", homeDir.getAbsolutePath());
            throw new IllegalStateException(homeDir + " is not directory");
        }
        return homeDir;
    }
}
