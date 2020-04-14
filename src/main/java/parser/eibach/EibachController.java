package parser.eibach;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import parser.eibach.entiities.EibItem;
import parser.fox.FoxSiteSurfer;
import parser.utils.BasicUtils;
import parser.utils.HibernateUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EibachController {

    private static final Logger logger = LogManager.getLogger(EibachController.class.getName());

    public void savePagesToDisk(){
        WebDriver driver = initDriver();
        Set<String> pagesToSave = new HashSet<>(BasicUtils.getInputInfo());
        Set<String> savedPages = new EibachService().getSavedPages();
        pagesToSave.removeAll(savedPages);
        int total = pagesToSave.size();
        int counter = 0;
        int rebootCounter = 0;
        for (String url: pagesToSave){
            new EibPageSaver(driver, url).savePage();
            counter++;
            rebootCounter++;
            logger.info("saved page " + counter + " of total " + total);
            if (rebootCounter==100){
                driver.quit();
                driver = initDriver();
                rebootCounter = 0;
            }
        }

        driver.quit();
        HibernateUtil.shutdown();
    }

    public void processParsedPages(){
        Set<String> processedItems = EibachService.getProcessedItemsLinks();
        Set<String> itemsToCheck = EibachService.getAllItemsLinks();
        itemsToCheck.removeAll(processedItems);
        Map<String, String> nameLinkMap = getNameLinkMap(itemsToCheck);
        Set<String> pages = getFileNameSet();
        pages.forEach(page->{
            if (nameLinkMap.containsKey(page)){
                Document doc = null;
                try {
                     doc = Jsoup.parse(new String(Files.readAllBytes(Paths.get(page))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                EibItem item = new ItemBuilder(doc, nameLinkMap.get(page)).buildItem();
                EibachService.saveItemToDB(item);
            }
        });
    }

    private Set<String> getFileNameSet() {
        Set<String> result = new HashSet<>();
        try (Stream<Path> walk = Files.walk(Paths.get("C:\\pages\\eibach"))) {
            result  = walk.filter(Files::isRegularFile)
                    .map(Path::toString).collect(Collectors.toSet());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    //k - file name, v - item url
    private Map<String, String> getNameLinkMap(Set<String> itemsToCheck) {
        Map<String, String> result = new HashMap<>();
        itemsToCheck.forEach(link->{
            String fName = StringUtils.substringAfterLast(link, "/");
            result.put(fName, link);
        });

        return result;
    }

    private WebDriver initDriver() {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
        System.setProperty("webdriver.chrome.silentOutput", "true");

        return new ChromeDriver();
    }
}
