package parser.eibach;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import parser.utils.BasicUtils;
import parser.utils.SileniumUtil;

import java.io.IOException;

class EibPageSaver {
    private WebDriver driver;
    private String url;
    private By checkBy;
    private static final Logger logger = LogManager.getLogger(EibPageSaver.class.getName());

    void savePage(){
        openPage();
       if (savePageToFile()){
           EibachService.savePageLinkToDB(url);
       }
    }

    private boolean savePageToFile() {
        try {
            driver.findElement(checkBy);
        }
        catch (NoSuchElementException e){
            logger.error("no check element found");
            return false;
        }
        String pageSource = driver.getPageSource();
        String fNamePart = StringUtils.substringAfterLast(url, "/");
        String fName = "C:/pages/eibach/"+fNamePart;
        try {
            BasicUtils.saveTextToFile(pageSource, fName);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void openPage() {
        if (driver.getCurrentUrl().contains("eibach")){
            WebElement checkEl = driver.findElement(By.className("main-logo"));
            driver.get(url);
            while (true){
                try {
                    checkEl.isDisplayed();
                }
                catch (StaleElementReferenceException e){
                    break;
                }
                catch (TimeoutException e){
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        else {
            driver.get(url);
        }
       try {
           SileniumUtil.waitForElementPresent(driver, checkBy, 10);
       }
       catch (TimeoutException ignored){}
    }


    EibPageSaver(WebDriver driver, String url) {
        this.driver = driver;
        this.url = url;
        checkBy = By.className("bottom-newsletter");
    }


}
