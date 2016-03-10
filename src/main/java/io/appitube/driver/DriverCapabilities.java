package io.appitube.driver;

import io.appitube.util.SharedProperties;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if (desiredCapabilities == null) {
            switch (properties.getPlatform().toLowerCase()) {
                case "ios":
                    desiredCapabilities = getIOSDesiredCapabilities();
                    break;
                case "android":
                    desiredCapabilities = getAndroidDesiredCapabilities();
                    break;
                default:
                    logger.error("{" + properties.getPlatform() + "} is not a valid capability.");
                    throw new IllegalArgumentException("{" + properties.getPlatform() + "} is not a valid capability.");
            }
        }
        return desiredCapabilities;
    }

    public boolean isHardwareTypeARealIOSDevice() {
        return isHardwareTypeADevice() && properties.getPlatform().equalsIgnoreCase(MobilePlatform.IOS);
    }

    private DesiredCapabilities getAndroidDesiredCapabilities() {
        setDesiredCapabilities();

        // See for possible values: http://appium.io/slate/en/master/?java#appium-server-capabilities
        desiredCapabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, properties.getBundleId());
        desiredCapabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, properties.getAppActivity());

        if (isHardwareTypeADevice()) {
            desiredCapabilities.setCapability(AndroidMobileCapabilityType.ANDROID_DEVICE_READY_TIMEOUT, properties.getDeviceTimeout());
        } else {
            desiredCapabilities.setCapability(AndroidMobileCapabilityType.AVD_READY_TIMEOUT, properties.getLaunchTimeout());
        }


        if (!isHardwareTypeADevice()) {
            desiredCapabilities.setCapability(AndroidMobileCapabilityType.AVD, properties.getDeviceName());
        } else {
            desiredCapabilities.setCapability(MobileCapabilityType.UDID, properties.getDeviceId());
        }

        System.out.println(printDesiredCapabilities(desiredCapabilities));
        return desiredCapabilities;
    }

    private DesiredCapabilities getIOSDesiredCapabilities() {
        setDesiredCapabilities();

        // See for possible values: http://appium.io/slate/en/master/?java#appium-server-capabilities
        desiredCapabilities.setCapability(IOSMobileCapabilityType.BUNDLE_ID, properties.getBundleId());
        desiredCapabilities.setCapability(IOSMobileCapabilityType.LAUNCH_TIMEOUT, properties.getLaunchTimeout());
        desiredCapabilities.setCapability(IOSMobileCapabilityType.WAIT_FOR_APP_SCRIPT, String.format("$.delay(%s);$.acceptAlert()", properties.getAppScriptDelayTimeout()));

        if (isHardwareTypeADevice()) {
            desiredCapabilities.setCapability(MobileCapabilityType.UDID, properties.getDeviceId());
        }

        System.out.println(printDesiredCapabilities(desiredCapabilities));
        return desiredCapabilities;
    }

    private void setDesiredCapabilities() {
        desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, properties.getPlatform());
        desiredCapabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, properties.getPlatformVersion());
        desiredCapabilities.setCapability("hardwareType", properties.getHardwareType());
        desiredCapabilities.setCapability(MobileCapabilityType.APP, properties.getApp());
        desiredCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, properties.getDeviceName());
        desiredCapabilities.setCapability(MobileCapabilityType.NO_RESET, properties.getNoReset());
        desiredCapabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, properties.getDeviceTimeout());
        desiredCapabilities.setCapability("name", "" + " - " + (System.getenv("BUILD_NUMBER") != null ? System.getenv("BUILD_NUMBER") : System.getenv("USER")));
        desiredCapabilities.setCapability(MobileCapabilityType.FULL_RESET, properties.getFullReset());

        desiredCapabilities.setCapability("autoLaunch",
                isHardwareTypeARealIOSDevice() ? true : properties.getAutoLaunch());
    }

    private boolean isHardwareTypeADevice() {
        return properties.getHardwareType().equalsIgnoreCase("device");
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
