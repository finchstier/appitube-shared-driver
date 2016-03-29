package io.appitube.driver;

import io.appitube.util.SharedProperties;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.appium.java_client.remote.AndroidMobileCapabilityType.*;
import static io.appium.java_client.remote.IOSMobileCapabilityType.*;
import static io.appium.java_client.remote.MobileCapabilityType.*;

public class DriverCapabilities {

    private DesiredCapabilities desiredCapabilities;
    private SharedProperties properties;
    private static Logger logger = LoggerFactory.getLogger(DriverCapabilities.class);

    public DriverCapabilities() {
        super();
        this.properties = getSharedProperties();
    }

    protected SharedProperties getSharedProperties() {
        if (properties == null) {
            properties = new SharedProperties();
            properties.setProperties();
        }
        return properties;
    }

    public DesiredCapabilities getDesiredCapabilities() {
        final String platform = properties.getPlatform();

        if (desiredCapabilities == null) {
            switch (platform.toLowerCase()) {
                case "ios":
                    desiredCapabilities = getIOSDesiredCapabilities();
                    break;
                case "android":
                    desiredCapabilities = getAndroidDesiredCapabilities();
                    break;
                default:
                    logger.error("{" + platform + "} is not a valid capability.");
                    throw new IllegalArgumentException("{" + platform + "} is not a valid capability.");
            }
        }
        return desiredCapabilities;
    }

    public boolean isHardwareTypeRealIOSDevice() {
        return isHardwareTypeADevice() && isPlatformIOS();
    }

    private boolean isPlatformIOS() {
        return properties.getPlatform().equalsIgnoreCase(MobilePlatform.IOS);
    }

    private DesiredCapabilities getAndroidDesiredCapabilities() {
        setDesiredCapabilities();

        // List of Android-specific capabilities: https://github.com/appium/appium/blob/1.5/docs/en/writing-running-appium/caps.md#android-only
        desiredCapabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, properties.getBundleId());
        desiredCapabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, properties.getAppActivity());

        if (isHardwareTypeADevice()) {
            desiredCapabilities.setCapability(ANDROID_DEVICE_READY_TIMEOUT, properties.getDeviceTimeout());
        } else {
            desiredCapabilities.setCapability(AVD, properties.getDeviceName());
            desiredCapabilities.setCapability(AVD_LAUNCH_TIMEOUT, properties.getLaunchTimeout());
            desiredCapabilities.setCapability(AVD_READY_TIMEOUT, properties.getLaunchTimeout());
        }

        System.out.println(printDesiredCapabilities(desiredCapabilities));
        return desiredCapabilities;
    }

    private DesiredCapabilities getIOSDesiredCapabilities() {
        setDesiredCapabilities();

        // List of iOS-specific capabilities: https://github.com/appium/appium/blob/1.5/docs/en/writing-running-appium/caps.md#ios-only
        desiredCapabilities.setCapability(BUNDLE_ID, properties.getBundleId());
        desiredCapabilities.setCapability(AUTO_ACCEPT_ALERTS, properties.getAutoAlert());
        desiredCapabilities.setCapability(IOSMobileCapabilityType.LAUNCH_TIMEOUT, properties.getLaunchTimeout());
        desiredCapabilities.setCapability(WAIT_FOR_APP_SCRIPT, String.format("$.delay(%s);$.acceptAlert()", properties.getAppScriptDelayTimeout()));

        System.out.println(printDesiredCapabilities(desiredCapabilities));
        return desiredCapabilities;
    }

    private void setDesiredCapabilities() {
        final String buildNumber = System.getenv("BUILD_NUMBER");
        final String user = System.getenv("USER");
        desiredCapabilities = new DesiredCapabilities();

        // List of capabilities: https://github.com/appium/appium/blob/1.5/docs/en/writing-running-appium/caps.md#appium-server-capabilities
        desiredCapabilities.setCapability(PLATFORM_NAME, properties.getPlatform());
        desiredCapabilities.setCapability(PLATFORM_VERSION, properties.getPlatformVersion());
        desiredCapabilities.setCapability(APP, properties.getApp());
        desiredCapabilities.setCapability(FULL_RESET, properties.getFullReset());
        desiredCapabilities.setCapability(NO_RESET, properties.getNoReset());
        desiredCapabilities.setCapability(NEW_COMMAND_TIMEOUT, properties.getDeviceTimeout());
        desiredCapabilities.setCapability("autoLaunch", properties.getAutoLaunch());
        desiredCapabilities.setCapability("name", "" + " - " + (buildNumber != null ? buildNumber : user));

        if (isHardwareTypeADevice()) {
            desiredCapabilities.setCapability(UDID, properties.getDeviceId());
            desiredCapabilities.setCapability(DEVICE_NAME, properties.getDeviceId());
        } else {
            desiredCapabilities.setCapability(DEVICE_NAME, properties.getDeviceName());
        }
    }

    private boolean isHardwareTypeADevice() {
        if (properties.getDeviceId() != null) {
            return true;
        }
        return false;
    }

    String printDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
        String capabilities = desiredCapabilities.toString();

        String printCapabilities = "\nDesired Capabilities: ";
        printCapabilities = printCapabilities + "\n" + capabilities.replace("Capabilities [{", "");
        printCapabilities = printCapabilities.replace("}]", "");
        printCapabilities = printCapabilities.replaceAll(", ", "\n");
        return printCapabilities + "\n";
    }
}
