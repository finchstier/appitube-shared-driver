package io.appitube.driver;

import io.appitube.util.SharedProperties;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServerHasNotBeenStartedLocallyException;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class DriverServer {

    private AppiumDriverLocalService localService;
    private DriverCapabilities driverCapabilities;
//    private DesiredCapabilities serverCapabilities;
    private final SharedProperties properties;
    private final String serverHost;
    private final String logLevel;
    private final String logPath = System.getProperty("user.dir") + File.separator + "appium.log";
    private File logFile = new File(logPath);
    private static Logger logger = LoggerFactory.getLogger(DriverServer.class);

    public DriverServer() {
        super();
        this.driverCapabilities = getDriverCapabilities();
        this.properties = driverCapabilities.getSharedProperties();
        this.serverHost = properties.getServerHost();
        this.logLevel = properties.getLogLevel();
    }

    protected DriverCapabilities getDriverCapabilities() {
        if (driverCapabilities == null) {
            driverCapabilities = new DriverCapabilities();
        }
        return driverCapabilities;
    }

    public void setDriverServer() {
        String command = "appium --log=" + logPath + " --log-timestamp";

        do {
            try {
                System.out.println("Executing: " + command);

                setLocalService(getAppiumJSFile(), logFile);
                startLocalService();
            } catch (UnreachableBrowserException e) {
                logger.error("Unable to set Appium server, retrying service now...", e);
                System.out.println("Unable to set Appium server, retrying service now..." + e);
            }
        } while (!isLocalServiceRunning());
    }

    public URL getLocalServiceURL() {
        if (localService == null) {
            System.out.println("Appium local service is not running...");
            setDriverServer();
        }
        return localService.getUrl();
    }

    public boolean isLocalServiceRunning() {
        try {
            return localService.isRunning();
        } catch (Exception e) {
            logger.error("Appium local service is not running because service is ", e);
            System.out.println("Appium local service is not running because service is " + e.getMessage());
            return false;
        }
    }

    public void startLocalService() {
        if (!isLocalServiceRunning()) {
            logger.info("Starting local service on " + Platform.getCurrent());
            System.out.println("Starting Appium local service on " + Platform.getCurrent() + "...");

            try {
                getLocalService();
                localService.start();
                System.out.println("Appium local service was successfully started and running on " + getLocalServiceURL());
            } catch (AppiumServerHasNotBeenStartedLocallyException e) {
                logger.error("Unable to start Appium server locally.", e);
                throw new AppiumServerHasNotBeenStartedLocallyException("Unable to start Appium server locally.", e);
            }
        } else {
            logger.info("Local service is already running on " + getLocalServiceURL());
            System.out.println("Appium local service is already running on " + getLocalServiceURL());
        }
    }

    public void stopLocalService() {
        if (isLocalServiceRunning()) {
            logger.info("Stopping local service running on " + getLocalServiceURL());
            System.out.println("Stopping Appium local service running on " + getLocalServiceURL());

            try {
                localService.stop();
                localService = null;
            } catch (UnreachableBrowserException e) {
                logger.error("Unable to stop local service.", e);
                throw new UnreachableBrowserException("Unable to stop Appium local service.", e);
            }
            System.out.println("Appium local service was successfully stopped.");
        }
    }

    private void getLocalService() {
        if (localService == null) {
            setLocalService(getAppiumJSFile(), logFile);
        }
    }

    private void setLocalService(File appiumJSFile, File logFile) {
        localService = AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
                .usingDriverExecutable(getNodeExecutableFile())
                .withAppiumJS(appiumJSFile)
                .withIPAddress(serverHost)
                .usingAnyFreePort()
//                .withCapabilities(serverCapabilities)
                .withArgument(GeneralServerFlag.LOG_LEVEL, logLevel)
                .withLogFile(logFile)
                .withStartUpTimeOut(30, TimeUnit.SECONDS));
    }

    private File getNodeExecutableFile() {
        return new File(String.valueOf(SystemUtils.IS_OS_WINDOWS ?
                SystemUtils.OS_ARCH.equalsIgnoreCase("x86") ?
                        "C/Program Files (x86)/nodejs/node.exe"
                        : "C/Program Files/nodejs/node.exe"
                : "/usr/local/Cellar/node/5.0.0/bin/node"));
    }

    private File getAppiumJSFile() {
        return new File(String.valueOf(SystemUtils.IS_OS_WINDOWS ?
                SystemUtils.OS_ARCH.equalsIgnoreCase("x86") ?
                        "C:/Program Files (x86)/Appium/node_modules/appium/bin/appium.js"
                        : "C:/Program Files/Appium/node_modules/appium/bin/appium.js"
                : "/usr/local/lib/node_modules/appium/bin/appium.js"));
    }
}
