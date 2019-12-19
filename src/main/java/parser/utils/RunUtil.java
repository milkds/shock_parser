package parser.utils;

import org.openqa.selenium.WebDriver;

public class RunUtil {

    public static void shutDownWithError(WebDriver driver){
        driver.quit();
        HibernateUtil.shutdown();
        System.exit(1);
    }

    public static void shutDownWithoutError(WebDriver driver){
        driver.quit();
        HibernateUtil.shutdown();
        System.exit(0);
    }
}
