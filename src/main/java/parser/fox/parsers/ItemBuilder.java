package parser.fox.parsers;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import parser.fox.entities.FoxItem;
import parser.fox.entities.FoxItemSpec;
import parser.fox.entities.TableEntry;
import parser.utils.SileniumUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemBuilder {
    private static final Logger logger = LogManager.getLogger(ItemBuilder.class.getName());
    public FoxItem buildItem(TableEntry entry) {
        WebDriver driver = openItemPage(entry.getWebLink());
        FoxItem item = new FoxItem();
        setEntryFields(item, entry);
        setDriverFields(item, driver);
        driver.close();

        return item;
    }

    private void setDriverFields(FoxItem item, WebDriver driver) {
        setDesc(item, driver);
        setSpecs(item, driver);
        setPics(item, driver);
    }

    private void setEntryFields(FoxItem item, TableEntry entry) {
        setWeblink(item, entry);
        setPrice(item, entry);
        setPartNo(item, entry);
        setTitle(item, entry); //series and supQty must be set only after title is set
        setSeries(item);
        setSupplyQty(item);
    }

    private void setPartNo(FoxItem item, TableEntry entry) {
        item.setPartNo(entry.getPartNo());
    }

    private void setPrice(FoxItem item, TableEntry entry) {
        item.setPrice(entry.getPrice());
    }

    private void setPics(FoxItem item, WebDriver driver) {
        WebElement picKeeper = null;
        try {
            picKeeper = driver.findElement(By.cssSelector("div[class='content active']"));
            picKeeper = picKeeper.findElement(By.cssSelector("div[class='medium-6 columns']"));
        }
        catch (NoSuchElementException e){
            logger.error("NO pic holder at " + driver.getCurrentUrl());
            item.setItemPicUrls("NO PICS");
            return;
        }
        List<WebElement> imgElements = picKeeper.findElements(By.tagName("img"));
        if (imgElements.size()==0){
            logger.error("NO pic elements at pic holder " + driver.getCurrentUrl());
            item.setItemPicUrls("NO PICS");
            return;
        }
        StringBuilder picBuilder = new StringBuilder();
        imgElements.forEach(imgEl->{
            picBuilder.append(imgEl.getAttribute("src"));
            picBuilder.append(";;");
        });
        item.setItemPicUrls(picBuilder.toString());
    }

    private void setWeblink(FoxItem item, TableEntry entry) {
        item.setWebLink(entry.getWebLink());
    }

    private void setSpecs(FoxItem item, WebDriver driver) {
        WebElement specEl = null;
        try {
            specEl = driver.findElement(By.cssSelector("div[class='medium-6 columns acespies-specs']"));
        }
        catch (NoSuchElementException e){
            logger.error("No Spec table element at " + driver.getCurrentUrl());
            return;
        }
        try {
            specEl = specEl.findElement(By.tagName("table"));
        }
        catch (NoSuchElementException e){
            logger.error("No table at spec el at " + driver.getCurrentUrl());
            return;
        }
        List<WebElement> tableRows = specEl.findElements(By.tagName("tr"));
        Set<FoxItemSpec> specs = new HashSet<>();
        tableRows.forEach(row->{
            WebElement nameEl = null;
            WebElement valEl = null;
            try {
                nameEl = row.findElement(By.tagName("th"));
                valEl = row.findElement(By.tagName("td"));
            }
            catch (NoSuchElementException e){
                return;
            }
            FoxItemSpec spec = new FoxItemSpec();
            spec.setSpecName(nameEl.getText());
            spec.setSpecVal(valEl.getText());
            if (spec.getSpecName().equals("Fitment notes")){
                return;
            }
            specs.add(spec);
        });
        item.setSpecs(specs);
        setDocs(item, driver);
    }

    private void setDocs(FoxItem item, WebDriver driver) {
        WebElement docEl = null;
        try {
            docEl = driver.findElement(By.className("manuals"));
        }
        catch (NoSuchElementException e){
            logger.error("No docs el at " + driver.getCurrentUrl());
            return;
        }
        List<WebElement> docLinkEls = docEl.findElements(By.tagName("a"));
        if (docLinkEls.size()==0){
            logger.error("No links at docs el at " + driver.getCurrentUrl());
            return;
        }
        Set<FoxItemSpec> docSpecs = new HashSet<>();
        docLinkEls.forEach(docLinkEl->{
            FoxItemSpec spec = new FoxItemSpec();
            spec.setSpecName(docLinkEl.getText());
            spec.setSpecVal(docLinkEl.getAttribute("href"));
            docSpecs.add(spec);
        });
        item.getSpecs().addAll(docSpecs);
    }

    private void setDesc(FoxItem item, WebDriver driver) {
        WebElement descEl = null;
        try {
            descEl = driver.findElement(By.cssSelector("div[class='medium-6 columns acespies-specs']"));
        }
        catch (NoSuchElementException e){
            logger.error("No description for item " + driver.getCurrentUrl());
            item.setDescription("NO DESC");
            return;
        }

        List<WebElement> descPartsEls = descEl.findElements(By.tagName("p"));
        if (descPartsEls.size()==0){
            logger.error("No description tags for item " + driver.getCurrentUrl());
            item.setDescription("NO DESC");
            return;
        }
        StringBuilder descBuilder = new StringBuilder();
        descPartsEls.forEach(descPartsEl->{
            descBuilder.append(descPartsEl.getText());
            descBuilder.append(System.lineSeparator());
        });
        int length = descBuilder.length();
        if (length>2){
            descBuilder.setLength(length-2);
        }
        item.setDescription(descBuilder.toString());
    }

    private void setTitle(FoxItem item, TableEntry entry) {
        item.setTitle(entry.getTitle());
    }

    private void setSupplyQty(FoxItem item) {
        String title = item.getTitle();
        String supQty = "Single";
        if (title.contains("(")&&title.contains(")")){
            supQty = StringUtils.substringBetween(title, "(", ")");
        }
        item.setSupplyQty(supQty);
    }

    private void setSeries(FoxItem item) {
        String title = item.getTitle();
        String series = StringUtils.substringBefore( title," Series");
        if (series==null||series.length()==0){
           series = "NO";
        }
        series = series + " SERIES";
        item.setSeries(series);
    }

    private WebDriver openItemPage(String webLink) {
        WebDriver driver = launchDriver(webLink);

        int attempts = 0;
        logger.debug("waiting for item data to become visible");
        while (true){
            try {
                SileniumUtil.waitForElementVisible(driver, By.id("family-models-data"));
                break;
            }
            catch (TimeoutException e){
                logger.debug("retrying...");
                if (attempts==5){
                    driver.close();
                    driver = launchDriver(webLink);
                    attempts = 0;
                }
                attempts++;
            }
        }
        logger.debug("Page loaded: " + webLink);

        return driver;
    }

    private WebDriver launchDriver(String link){
        WebDriver driver = new ChromeDriver();
        driver.get(link);

        return driver;
    }
}
