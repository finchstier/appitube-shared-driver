package io.appitube.driver;

import io.appitube.util.FindBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ElementFinder implements IElementFinder {

    private SharedDriver sharedDriver = getSharedDriver();
    private final AppiumDriver driver = sharedDriver.getDriver();
    public static final long TIMEOUT = 15;
    private static Logger logger = LoggerFactory.getLogger(ElementFinder.class);

    public ElementFinder() {
        super();
    }

    public SharedDriver getSharedDriver() {
        if (sharedDriver == null) {
            sharedDriver = new SharedDriver();
        }
        return sharedDriver;
    }

    public Wait<AppiumDriver> fluentWait(final long timeout) {
        return new FluentWait<>(driver)
                .withTimeout(timeout, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class)
                .ignoring(TimeoutException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    public Wait<AppiumDriver> fluentWait() {
        return fluentWait(TIMEOUT);
    }

    @Override
    public boolean isElementDisplayed(final MobileElement element) {
        return fluentWait().until(ExpectedConditions.visibilityOf(element)).isDisplayed();
    }

    @Override
    public boolean isElementSelected(final MobileElement element) {
        return fluentWait().until(ExpectedConditions.elementToBeSelected(element));
    }

    @Override
    public boolean isElementChecked(final FindBy findBy, final String finder) {
        return Boolean.parseBoolean(waitUntilVisibilityOfElement(findBy, finder).getAttribute("checked"));
    }

    @Override
    public boolean waitUntilInvisibilityOfElement(final FindBy findBy, final String finder) {
        return fluentWait().until(ExpectedConditions.invisibilityOfElementLocated(findBy.using(finder)));
    }

    @Override
    public boolean waitUntilInvisibilityOfElementWithText(final FindBy findBy, final String finder, final String text) {
        return fluentWait().until(ExpectedConditions.invisibilityOfElementWithText(findBy.using(finder), text));
    }

    @Override
    public boolean waitUntilTextIsPresentInElement(final MobileElement element, final String text) {
        return fluentWait().until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    @Override
    public MobileElement findElementById(final String id) {
        return waitUntilVisibilityOfElement(FindBy.ID, id);
    }

    @Override
    public MobileElement waitUntilVisibilityOfElementById(final String id) {
        try {
            return findElementById(id);
        } catch (TimeoutException e) {
            String errorMessage = "Timed out trying to locate element with id {" + id + "}, continuing... ";
            logger.error(errorMessage, e.getMessage());
            System.out.println(errorMessage + e.getMessage());
        }
        return null;
    }

    @Override
    public List<MobileElement> waitUntilVisibilityOfElementsById(final String id) {
        return waitUntilVisibilityOfElements(FindBy.ID, id);
    }

    @Override
    public MobileElement findElementByClassName(final String className) {
        return waitUntilVisibilityOfElement(FindBy.CLASS_NAME, className);
    }

    @Override
    public MobileElement waitUntilVisibilityOfElementByClassName(final String className) {
        try {
            return findElementByClassName(className);
        } catch (TimeoutException e) {
            String errorMessage = "Timed out trying to locate element with class name {" + className + "}, continuing... ";
            logger.error(errorMessage, e.getMessage());
            System.out.println(errorMessage + e.getMessage());
        }
        return null;
    }

    @Override
    public List<MobileElement> waitUntilVisibilityOfElementsByClassName(final String className) {
        return waitUntilVisibilityOfElements(FindBy.CLASS_NAME, className);
    }

    @Override
    public MobileElement findElementByAccessibilityId(final String accessibilityId) {
        return waitUntilVisibilityOfElement(FindBy.ACCESSIBILITY_ID, accessibilityId);
    }

    @Override
    public MobileElement waitUntilVisibilityOfElementByAccessibilityId(final String accessibilityId) {
        try {
            return findElementByAccessibilityId(accessibilityId);
        } catch (TimeoutException e) {
            String errorMessage = "Timed out trying to locate element with accessibility id {" + accessibilityId + "}, continuing... ";
            logger.error(errorMessage, e.getMessage());
            System.out.println(errorMessage + e.getMessage());
        }
        return null;
    }
    @Override
    public List<MobileElement> waitUntilVisibilityOfElementsByAccessibilityId(final String accessibilityId) {
        return waitUntilVisibilityOfElements(FindBy.ACCESSIBILITY_ID, accessibilityId);
    }


    @Override
    public MobileElement findElementByXpath(final String xpath) {
        return waitUntilVisibilityOfElement(FindBy.XPATH, xpath);
    }

    @Override
    public MobileElement waitUntilVisibilityOfElementByXpath(final String xpath) {
        try {
            return findElementByXpath(xpath);
        } catch (TimeoutException e) {
            String errorMessage = "Timed out trying to locate element with xpath {" + xpath + "}, continuing... ";
            logger.error(errorMessage, e.getMessage());
            System.out.println(errorMessage + e.getMessage());
        }
        return null;
    }

    @Override
    public List<MobileElement> waitUntilVisibilityOfElementsByXpath(final String xpath) {
        return waitUntilVisibilityOfElements(FindBy.XPATH, xpath);
    }

    @Override
    public List<MobileElement> waitUntilVisibilityOfElements(final FindBy findBy, final String finder) {
        try {
            return findElementsBy(findBy, finder);
        } catch (TimeoutException e) {
            String errorMessage = "Timed out trying to locate element by {" + findBy + ", " + finder + "}, continuing... ";
            logger.error(errorMessage, e.getMessage());
            System.out.println(errorMessage + e.getMessage());
        }
        return null;
    }

    @Override
    public MobileElement waitUntilElementIsClickable(final MobileElement element) {
        return (MobileElement) fluentWait().until(ExpectedConditions.elementToBeClickable(element));
    }

    @Override
    public Alert waitUntilAlertIsPresent() {
        return fluentWait().until(ExpectedConditions.alertIsPresent());
    }

    /*
        actions
     */
    public void click(MobileElement element) {
        waitUntilElementIsClickable(element).click();
    }

    public void sendKeys(MobileElement element, String value) {
        if (!element.getText().isEmpty()) {
            element.clear();
        }
        element.sendKeys(value);
    }

    public void acceptAlert() {
        waitUntilAlertIsPresent().accept();
    }

    public void dismissAlert() {
        waitUntilAlertIsPresent().dismiss();
    }

    private MobileElement waitUntilVisibilityOfElement(final FindBy findBy, final String finder) {
        return (MobileElement) fluentWait().until(ExpectedConditions.visibilityOfElementLocated(findBy.using(finder)));
    }

    private List<MobileElement> findElementsBy(final FindBy findBy, final String locator) {
        List<WebElement> elements = fluentWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(findBy.using(locator)));
        return convertWebElementsToMobileElements(elements);
    }

    private List<MobileElement> convertWebElementsToMobileElements(List<WebElement> webElements) {
        return webElements
                .stream()
                .map(webElement -> (MobileElement) webElement)
                .collect(Collectors.toList());
    }
}
