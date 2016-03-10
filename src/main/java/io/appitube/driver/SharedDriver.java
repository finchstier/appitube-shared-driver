package io.appitube.driver;

import io.appitube.util.SharedProperties;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

public class SharedDriver {

    private DriverServer driverServer;
    private final DriverCapabilities capabilities;
    private AppiumDriver driver;
    private final SharedProperties properties;

    public SharedDriver() {
        super();
        this.driverServer = getDriverServer();
        this.capabilities = driverServer.getDriverCapabilities();
        this.properties = capabilities.getSharedProperties();
    }

    protected DriverServer getDriverServer() {
        if (driverServer == null) {
            driverServer = new DriverServer();
        }
        return driverServer;
    }

    public AppiumDriver getDriver() {
        if (driver == null) {
            System.out.println("Appium driver is not currently set.");
            driver = initiateSharedDriver();
            System.out.println("Appium driver was successfully set to {" + driver + "}...");
        }
        return driver;
    }

    public void quit() {
        System.out.println("Quiting Appium driver {" + driver + "}...");
        if (driver != null) {
            try {
                driver.quit();
                driver = null;
                System.out.println("Appium driver was successfully quited.");
            } catch (UnreachableBrowserException e) {
                System.out.println("Unable to quit Appium driver, due to: " + e.getMessage());
            }
            driverServer.stopLocalService();
        }
    }

    public void launchApp() {
        if (!capabilities.isHardwareTypeARealIOSDevice() && !properties.getAutoLaunch()) {
            getDriver().launchApp();
        }
    }

    public AppiumDriver initiateSharedDriver() {
        int count = 1;
        do {
            if (driverServer.getLocalServiceURL() == null) {
                count++;
                if (count > 3) {
                    throw new UnreachableBrowserException("Shutting down due to unable to reach Appium server");
                } else {
                    System.out.println("Attempting to retry launching Appium server, this is retry #" + count);
                    driverServer.setDriverServer();
                }
            }
        } while (!driverServer.isLocalServiceRunning());

        switch (properties.getPlatform().toLowerCase()) {
            case "ios":
                driver = getIOSDriver();
                break;
            case "android":
                driver = getAndroidDriver();
                break;
            default:
                throw new IllegalArgumentException("{" + properties.getPlatform() + "} is not a valid capability.");
        }

        if (driver != null) {
            System.out.println(properties.getPlatform() + " driver was successfully initiated.");
            System.out.println("Driver {" + driver + "} is now running...");
        }
        return driver;
    }

    private AppiumDriver getAndroidDriver() {
        return driver = new AndroidDriver(driverServer.getLocalServiceURL(), capabilities.getDesiredCapabilities());
    }

    private AppiumDriver getIOSDriver() {
        return driver = new IOSDriver<>(driverServer.getLocalServiceURL(), capabilities.getDesiredCapabilities());
    }
}
