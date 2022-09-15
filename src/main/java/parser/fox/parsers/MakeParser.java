package parser.fox.parsers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import parser.fox.FoxDBService;
import parser.fox.dao.FoxCarDAO;
import parser.fox.entities.FoxCar;
import parser.utils.HibernateUtil;
import parser.utils.SileniumUtil;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class MakeParser {
    private static final Logger logger = LogManager.getLogger(MakeParser.class.getName());
    private int makeID;
    private WebDriver driver;
    private By makeBy; //this needed in case fox changes site code
    private int year;

    public void parseMake(Session session) {
        String modelElementID = "shockfinder-model";
        By modelBy = By.id(modelElementID);
        SileniumUtil.selectOptionByOptionID(makeID, makeBy, driver); //selecting make at make dropDown
        SileniumUtil.waitForSelectBy(driver, modelBy); //waiting for model dropdown to become active
     //   Session session = HibernateUtil.getFoxSessionFactory().openSession();
        int attempts = 0;
        Set<FoxCar> makeCars = null;
        while (true){
            try {
                makeCars = checkAllModels(modelBy, session);
                break;
            }
            catch (TimeoutException e){
                if (attempts==5){
                    logger.error("Couldn't get info for make with id " + makeID + " for year " + year);
                    throw e;
                }
                    attempts++;
            }
        }

        FoxDBService.saveCars(makeCars, session);

    }

    private Set<FoxCar> checkAllModels(By modelBy, Session session) {
        Set<FoxCar> result = new HashSet<>();
        Select modelSelect = SileniumUtil.getSelectBy(driver, modelBy);
        for (int i = 1; i < modelSelect.getOptions().size(); i++) {
            WebElement tableEl = openShockListForModel(i, modelBy); //throws TimeOutException
            FoxCar templateCar = buildTemplateCar(modelSelect);
            Set<FoxCar> carsFromModel = new ModelParser(driver, session, templateCar).getCarsForModel(tableEl);
            result.addAll(carsFromModel);
        }
        return result;
    }

    private FoxCar buildTemplateCar(Select modelSelect) {
        FoxCar result = new FoxCar();
        WebElement makeDrop = driver.findElement(makeBy);
        Select makeSelect = new Select(makeDrop);
        String make = makeSelect.getFirstSelectedOption().getText();

        result.setYear(year);
        result.setMake(make);
        result.setModel(modelSelect.getFirstSelectedOption().getText());

        return result;
    }

    private WebElement openShockListForModel(int modelIndex, By modelBy) {
        By positionBy = By.id("shockfinder-position");
        WebElement tableEl = null;
        int attempts = 0;
        while (true){
            SileniumUtil.selectOptionByOptionID(modelIndex, modelBy, driver); //selecting model
            SileniumUtil.waitForSelectBy(driver, positionBy); // waiting for position select to become available
            SileniumUtil.selectOptionByOptionID(1, positionBy, driver);
            try {
                tableEl = waitForShockListLoad();
                break;
            }
            catch (TimeoutException e){
                attempts++;
                if (attempts==5){
                    logger.error("Couldn't open shockList");
                    throw e;
                }
            }
        }

        return tableEl;
    }

    private WebElement waitForShockListLoad() {
        WebElement result = null;
        try {
            result = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(10))
                    .pollingEvery(Duration.ofMillis(50))
                    .ignoring(WebDriverException.class)
                    .until(ExpectedConditions.visibilityOfElementLocated(By.id("fitguide")));
        }
        catch (TimeoutException e){

        }

        return result;
    }


    public MakeParser(int makeID, WebDriver driver, By makeBy, int year) {
        this.makeID = makeID;
        this.driver = driver;
        this.makeBy = makeBy;
        this.year = year;
    }
    public int getMakeID() {
        return makeID;
    }
    public WebDriver getDriver() {
        return driver;
    }
    public By getMakeBy() {
        return makeBy;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
}
