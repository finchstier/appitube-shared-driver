package io.appitube;

import io.appitube.driver.ElementFinder;
import io.appitube.driver.SharedDriver;
import io.appitube.util.FindBy;
import io.appium.java_client.MobileElement;
import org.junit.Assert;

import java.util.List;

public class UnitTest {
    private SharedDriver sharedDriver;
    private ElementFinder elementFinder = getElementFinder();
    private MobileElement element;
    private final String appPackage = "com.commonagency.moonpig.uk.debug:id/";
    static UnitTest unitTest;

    public static void main(String... args) {
        unitTest = new UnitTest();

        unitTest.setup();
        unitTest.sharedDriver.launchApp();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        unitTest.runTest();
        unitTest.teardown();
    }

    private void setup() {
        try {
            sharedDriver = elementFinder.getSharedDriver();
            sharedDriver.getDriver();
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void teardown() {
        try {
            Thread.sleep(5000);
            sharedDriver.quit();
            sharedDriver = null;
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ElementFinder getElementFinder() {
        if (elementFinder == null) {
            elementFinder = new ElementFinder();
        }
        return elementFinder;
    }

    private void runTest() {
        selectAProduct();
        login();
        selectAnAddress();
        checkout();
    }

    private void selectAProduct() {
        this.element = elementFinder.waitUntilVisibilityOfElementById(appPackage + "homepage_banner");
        elementFinder.click(element);

        List<MobileElement> products = elementFinder.waitUntilVisibilityOfElementsById(appPackage + "card_item_thumbnail_image");
        if (products.size() > 0) {
            elementFinder.click(products.get(0));
        }

        Assert.assertTrue(elementFinder.waitUntilInvisibilityOfElement(FindBy.ID, appPackage + "product_loading_spinner"));

        List<MobileElement> pageIndicators = elementFinder.findElementById(appPackage + "page_indicator").findElementsByClassName("android.view.View");

        int attempts = pageIndicators.size() - 1;
        int attempt = 0;
        do {
            this.element = elementFinder.findElementById(appPackage + "next_button");
            elementFinder.click(element);
            System.out.println("Attempt number: " + attempt);
            attempt++;
        } while (attempt != attempts);

        this.element = elementFinder.waitUntilVisibilityOfElementById(appPackage + "order_now_button");
        elementFinder.click(element);
    }

    private void login() {
        if (elementFinder.isElementDisplayed(elementFinder.findElementById("login_sign_in_button"))) {
            MobileElement usernameField = elementFinder.findElementById(appPackage + "login_email_edit_text");
            elementFinder.sendKeys(usernameField, "vapiano@test.moonpig.com");

            MobileElement passwordField = elementFinder.findElementById(appPackage + "login_password_edit_text");
            elementFinder.sendKeys(passwordField, "Password1");

            elementFinder.click(elementFinder.findElementById("login_sign_in_button"));
        }
    }

    private void selectAnAddress() {
        List<MobileElement> addresses = elementFinder.waitUntilVisibilityOfElementById(appPackage + "address_list_recycler_view")
                .findElementsById(appPackage + "address_item_root");
        if (addresses.size() > 0) {
            elementFinder.click(addresses.get(0));
        }
    }

    private void checkout() {
        if(elementFinder.waitUntilInvisibilityOfElement(FindBy.CLASS_NAME, "android.widget.ProgressBar")) {
            elementFinder.click(elementFinder.findElementById(appPackage + "pay_for_order"));
        }
    }

    private void payment() {
//        MobileElement cardNumber = elementFinder.findElementById(appPackage + "");
    }
}
