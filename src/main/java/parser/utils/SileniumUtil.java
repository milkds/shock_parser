package parser.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.UnexpectedTagNameException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SileniumUtil {
    private static final Logger logger = LogManager.getLogger(SileniumUtil.class.getName());

    //returns null, if exception occurs
    public static Select getSelectBy(WebDriver driver, By dropBy) {
        WebElement drop = null;
        try {
            drop = driver.findElement(dropBy);
        }
        catch (NoSuchElementException e){
            logger.error("No expected element");
            return null;
        }
        Select result = null;
        try {
            result = new Select(drop);
        }
        catch (UnexpectedTagNameException e){
            logger.error("Element is not Select " + drop.getAttribute("innerHTML") );
        }

        return result;
    }

    public static int getElementIDfromSelect(Select select, String elementString) {
        int result = 0;
        if (select==null){
            return result;
        }
        List<WebElement> optionsList = select.getOptions();
        List<String> optionsTextList = new ArrayList<>();

        optionsList.forEach(optionEl->{
            optionsTextList.add(optionEl.getText());
        });
        for (int i = 0; i < optionsTextList.size(); i++) {
            if (optionsTextList.get(i).equals(elementString)){
                result = i;
                break;
            }
        }

        return result;
    }

    public static void selectOptionByID(int id, String elemHTMLid, WebDriver driver) {
        By by = By.id(elemHTMLid);
        selectOptionByOptionID(id, by, driver);
    }

    public static void waitForSelectBy(WebDriver driver, By by) {
        Select select = null;
        int attempts = 0;
        while (true){
            select = getSelectBy(driver, by);
            if (select!=null){
                List<WebElement> options = select.getOptions();
                if (options.size()>1){
                    break;
                }
            }
            attempts++;
            if (attempts==10){
                throw new TimeoutException();
            }
            sleepForMS(1000);
        }

    }

    private static void sleepForMS(int i) {
        try {
            Thread.sleep(i);
        }
        catch (InterruptedException ignored){}
    }

    public static int getOptionsQtyInSelect(WebDriver driver, By by) {
        Select select = getSelectBy(driver, by);
        if (select==null){
            return 0;
        }

        return select.getOptions().size();
    }

    public static void selectOptionByOptionID(int id, By by, WebDriver driver) {
        Select select = getSelectBy(driver, by);
        select.selectByIndex(id);
    }

    public static WebElement waitForElementVisible(WebDriver driver, By by) {
        WebElement result = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(60))
                .pollingEvery(Duration.ofMillis(50))
                .ignoring(WebDriverException.class)
                .until(ExpectedConditions.visibilityOfElementLocated(by));

        return result;
    }

    public static WebElement waitForElementVisible(WebDriver driver, By by, int seconds) {
        WebElement result = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(seconds))
                .pollingEvery(Duration.ofMillis(50))
                .ignoring(WebDriverException.class)
                .until(ExpectedConditions.visibilityOfElementLocated(by));

        return result;
    }

    public static WebElement waitForElementPresent(WebDriver driver, By by, int seconds) {
        WebElement result = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(seconds))
                .pollingEvery(Duration.ofMillis(50))
                .ignoring(WebDriverException.class)
                .until(ExpectedConditions.presenceOfElementLocated(by));

        return result;
    }
}
