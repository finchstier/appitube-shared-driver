package io.appitube.driver;

import io.appitube.util.FindBy;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.Alert;

import java.util.List;

public interface IElementFinder {
    boolean isElementDisplayed(MobileElement element);

    boolean isElementSelected(MobileElement element);

    boolean waitUntilInvisibilityOfElement(FindBy findBy, String finder);

    boolean waitUntilInvisibilityOfElementWithText(FindBy findBy, String finder, String text);

    boolean waitUntilTextIsPresentInElement(MobileElement element, String text);

    MobileElement findElementById(String id);

    MobileElement waitUntilVisibilityOfElementById(String id);

    List<MobileElement> waitUntilVisibilityOfElementsById(String id);

    MobileElement findElementByClassName(String className);

    MobileElement waitUntilVisibilityOfElementByClassName(String className);

    List<MobileElement> waitUntilVisibilityOfElementsByClassName(String className);

    MobileElement findElementByAccessibilityId(String accessibilityId);

    MobileElement waitUntilVisibilityOfElementByAccessibilityId(String accessibilityId);

    List<MobileElement> waitUntilVisibilityOfElementsByAccessibilityId(String accessibilityId);

    MobileElement findElementByXpath(String xpath);

    MobileElement waitUntilVisibilityOfElementByXpath(String xpath);

    List<MobileElement> waitUntilVisibilityOfElementsByXpath(String xpath);

    List<MobileElement> waitUntilVisibilityOfElements(FindBy findBy, String finder);

    MobileElement waitUntilElementIsClickable(MobileElement element);

    Alert waitUntilAlertIsPresent();
}
