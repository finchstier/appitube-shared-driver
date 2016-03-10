package io.appitube.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SharedProperties {

    private final Map<String,String> properties = new HashMap<>();
    private static Logger logger = LoggerFactory.getLogger(SharedProperties.class);

    public SharedProperties() {
        super();
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public String validateProperty(String property) {
        if (!properties.containsKey(property)) {
            logger.error("Could not find the property key {" + property + "}.");
            throw new RuntimeException("Could not find the property key {" + property + "}.");
        }

        if (Objects.equals(getProperty(property), "")) {
            logger.error(property + " was not set.");
            throw new RuntimeException(property + " was not set.");
        }

        return getProperty(property);
    }

    public boolean getFullReset() {
        return Boolean.parseBoolean(validateProperty("full.reset"));
    }

    public String getServerHost() {
        return validateProperty("server.host");
    }

    public String getLogLevel() {
        return validateProperty("log.level");
    }

    public String getPlatform() {
        return validateProperty("platform.name");
    }

    public String getDeviceName() {
        return validateProperty("device.name");
    }

    public String getApp() {
        return validateProperty("app");
    }

    public String getBundleId() {
        return validateProperty("bundle.id");
    }

    public String getPlatformVersion() {
        return validateProperty("platform.version");
    }

    public String getHardwareType() {
        return validateProperty("hardware.type");
    }

    public boolean getAutoLaunch() {
        return Boolean.parseBoolean(validateProperty("auto.launch"));
    }

    public int getDeviceTimeout() {
        return Integer.parseInt(validateProperty("device.timeout"));
    }

    public boolean getNoReset() {
        return Boolean.parseBoolean(validateProperty("no.reset"));
    }

    public String getAppActivity() {
        return validateProperty("app.activity");
    }

    public String getDeviceId() {
        return validateProperty("device.id");
    }

    public int getLaunchTimeout() {
        return Integer.parseInt(validateProperty("launch.timeout"));
    }

    public String getAppScriptDelayTimeout() {
        return validateProperty("delay.timeout");
    }

    private String getProperty(String key) {
        return properties.get(key);
    }

    private void setProperties(Properties properties) {
        properties.stringPropertyNames().forEach(p -> setProperty(p, properties.getProperty(p)));
    }

    private Properties getProperties(InputStream inputStream) {
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error("There's no such properties file as {" + inputStream + "}.", e);
            throw new RuntimeException("There's no such properties file as {" + inputStream + "}.");
        }
        return properties;
    }
    
    public void setProperties() {
        setProperties(getProperties(getClass().getClassLoader().getResourceAsStream("default.properties")));
        setProperties(System.getProperties());
        String userDir = System.getProperty("user.dir");

        try {
            File profileDirectory = new File(userDir + "/src/test/resources/profiles/");

            if (profileDirectory.exists()) {
                File[] files = profileDirectory.listFiles();
                if (files == null || files.length == 0) {
                    logger.error("No profiles found in {user.dir}/src/test/resources/profiles/");
                    throw new IllegalStateException("No profiles found in {user.dir}/src/test/resources/profiles/");
                }
                List<File> potentialActiveProfiles = Arrays.asList(files).stream()
                        .filter(f -> f.getName().endsWith(".properties"))
                        .filter(f -> f.getName().startsWith("active-")).collect(Collectors.toList());

                if (potentialActiveProfiles.isEmpty()) {
                    logger.error("Must have at least one profile prefixed with 'active-'");
                    throw new IllegalStateException("Must have at least one profile prefixed with 'active-'");
                } else {
                    if (potentialActiveProfiles.size() > 1) {
                        logger.error("There can only be one profile marked as active.");
                        throw new IllegalStateException("There can only be one profile marked as active.");
                    } else {
                        File activeProfile = potentialActiveProfiles.get(0);
                        setProperties(getProperties(new FileInputStream(activeProfile)));
                    }
                }
            } else {
                logger.error("No {user.dir}/src/test/resources/profiles/ folder found.");
                throw new IllegalStateException("No {user.dir}/src/test/resources/profiles/ folder found.");
            }
        } catch (FileNotFoundException e) {
            logger.error("The requested file was not found in " + userDir, e);
            throw new IllegalArgumentException("The requested file was not found in " + userDir, e);
        }
    }
}
