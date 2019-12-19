package parser.fox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import parser.fox.parsers.MakeParser;
import parser.utils.RunUtil;
import parser.utils.SileniumUtil;

public class FoxSiteSurfer {
    private static final String SITE_URL = "https://www.ridefox.com/subhome.php?m=truck";
    private static final Logger logger = LogManager.getLogger(FoxSiteSurfer.class.getName());

    public static void parseSite(){
        StartPoint startPoint = getStartPoint();
        WebDriver driver = initDriver();
        launchParse(driver, startPoint);
    }

    private static void launchParse(WebDriver driver, StartPoint startPoint) {
        String yearElemID = "shockfinder-year";
        int yearID = getElementIDByID(yearElemID, driver, startPoint.getYear());
        if (yearID<2){
            yearID = 2;
        }
        SileniumUtil.selectOptionByID(yearID, yearElemID, driver);
        String makeElemID = "shockfinder-make";
        By makeBy = By.id(makeElemID);
        SileniumUtil.waitForSelectBy(driver, makeBy);

        int makeID = getElementIDByID(makeElemID, driver, startPoint.getMake());
        int totalMakesForYear = SileniumUtil.getOptionsQtyInSelect(driver, makeBy);
        if (makeID==totalMakesForYear-1){
            makeID = 1;
            yearID++;
        }
        else {
            makeID++; //increasing by one to start from new make
        }
        int totalYears = SileniumUtil.getOptionsQtyInSelect(driver, By.id(yearElemID));
        if (yearID==totalYears){
           logger.info("All years parsed");
           RunUtil.shutDownWithoutError(driver);
           return;
        }

        for (int i = yearID; i < totalYears; i++) {
            SileniumUtil.selectOptionByID(i, yearElemID, driver);
            SileniumUtil.waitForSelectBy(driver, makeBy);
            totalMakesForYear = SileniumUtil.getOptionsQtyInSelect(driver, makeBy);
            int year = getSelectedYear(driver, yearElemID);
            for (int j = makeID; j < totalMakesForYear; j++) {
                int attempts = 0;
                while (true){
                    try {
                        new MakeParser(makeID, driver, makeBy, year).parseMake();
                        break;
                    }
                    catch (TimeoutException e){
                        if (attempts==5){
                            RunUtil.shutDownWithError(driver);
                        }
                        attempts++;
                    }
                }
            }
            makeID = 1;
        }


    }

    private static int getSelectedYear(WebDriver driver, String yearElemID) {
        WebElement yearDrop = driver.findElement(By.id(yearElemID));
        Select yearSelect = new Select(yearDrop);
        String yearStr = yearSelect.getFirstSelectedOption().getText();

        return Integer.parseInt(yearStr);
    }

    private static int getElementIDByID(String elementID, WebDriver driver, String elementText) {
        int result = 0; //0 id is always non-relevant text like year, make, model
        if (elementText==null){
            return result;
        }
        By dropBy = By.id(elementID);
        Select select = SileniumUtil.getSelectBy(driver, dropBy);
        if (select==null){
            RunUtil.shutDownWithError(driver);
        }
        result = SileniumUtil.getElementIDfromSelect(select, elementText);

        return result;
    }

    private static WebDriver initDriver() {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get(SITE_URL);
        try {
            By by = By.id("shockfinder-year");
          //  SileniumUtil.waitForElementVisible(driver, by, 20);
            SileniumUtil.waitForElementPresent(driver, by, 20);
        }
        catch (TimeoutException e){
            logger.error("Couldn't load start page " + SITE_URL);
            driver.quit();
            System.exit(1);
        }

        return driver;
    }


    private static StartPoint getStartPoint() {
        StartPoint result = FoxDBService.getStartPoint();
        return result;
    }
}
