package parser.eibach;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import parser.utils.BasicUtils;
import parser.utils.SileniumUtil;

import java.io.IOException;

class EibPageSaver {
    private WebDriver driver;
    private String url;
    private By checkBy;

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
            return false;
        }
        String pageSource = driver.getPageSource();
        String fName = "C:/pages/eibach/"+url+".html";
        try {
            BasicUtils.saveTextToFile(pageSource, fName);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private void openPage() {
        driver.get(url);
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
