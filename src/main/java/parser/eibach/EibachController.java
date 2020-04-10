package parser.eibach;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import parser.fox.FoxSiteSurfer;
import parser.utils.BasicUtils;
import parser.utils.HibernateUtil;

import java.util.HashSet;
import java.util.Set;

public class EibachController {

    private static final Logger logger = LogManager.getLogger(EibachController.class.getName());

    public void savePagesToDisk(){
        WebDriver driver = initDriver();
        Set<String> pagesToSave = new HashSet<>(BasicUtils.getInputInfo());
        Set<String> savedPages = new EibachService().getSavedPages();
        pagesToSave.removeAll(savedPages);
        int total = pagesToSave.size();
        int counter = 0;

        for (String url: pagesToSave){
            new EibPageSaver(driver, url).savePage();
            counter++;
            logger.info("saved page " + counter + " of total " + total);
        }

        driver.quit();
        HibernateUtil.shutdown();
    }

    private WebDriver initDriver() {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");

        return new ChromeDriver();
    }
}
