package io.appitube.util;

import com.google.common.base.Function;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.By;

public enum FindBy {

    ID(By::id),
    CLASS_NAME(By::className),
    ACCESSIBILITY_ID(MobileBy::AccessibilityId),
    XPATH(By::xpath);

    private Function<String, By> by;

    FindBy(Function<String, By> by) {
        this.by = by;
    }

    public By using(String finder) {
        return by.apply(finder);
    }
}
