package parser.fox.parsers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import parser.fox.FoxDBService;
import parser.fox.entities.FoxCar;
import parser.fox.entities.FoxFit;
import parser.fox.entities.FoxItem;
import parser.fox.entities.TableEntry;

import javax.enterprise.inject.Model;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ModelParser {

    private WebDriver driver;
    private Session session;
    private FoxCar templateCar;
    private static final Logger logger = LogManager.getLogger(ModelParser.class.getName());


    Set<FoxCar> getCarsForModel(WebElement itemTableEl){
        Set<FoxCar> result = new HashSet<>();
        Set<TableEntry> entries = getEntries(itemTableEl);
        entries.forEach(entry->{
            FoxCar currentCar = getCurrentCar(result, entry);
            FoxFit fit = processEntry(entry);
            fit.setCar(currentCar);
            currentCar.getFits().add(fit);
            result.add(currentCar);
        });

        return result;
    }

    private FoxFit processEntry(TableEntry entry) {
        FoxFit result = new FoxFit();
        String partNo = entry.getPartNo();
        FoxItem item = FoxDBService.getExistingItem(session, partNo);
        if (item == null){
            item = new ItemBuilder().buildItem(entry);
        }
        result.setFitNote(entry.getFitNote());
        result.setLift(entry.getLift());
        result.setPosition(entry.getPosition());
        result.setItem(item);
        item.getFitments().add(result);

        return result;
    }

    private FoxCar getCurrentCar(Set<FoxCar> carSet, TableEntry entry) {
        FoxCar resCar = new FoxCar(templateCar);
        resCar.setDrive(entry.getDrive());
        if (!carSet.contains(resCar)){
            return resCar; //new Car
        }
        for (FoxCar car: carSet){
            if (car.equals(resCar)){
                resCar = car; //browsing for existing car
                break;
            }
        }

        return resCar;
    }

    private Set<TableEntry> getEntries(WebElement itemTableEl) {
        Set<TableEntry> result = new HashSet<>();
        List<WebElement> entryElemList = itemTableEl.findElements(By.tagName("tr"));
        for (int i = 1; i < entryElemList.size(); i++) {
            WebElement rowEl = entryElemList.get(i);
            TableEntry entry = new EntryBuilder().buildEntry(rowEl);
            result.add(entry);
        }

        return result;
    }

    ModelParser(WebDriver driver, Session session, FoxCar templateCar) {
        this.driver = driver;
        this.session = session;
        this.templateCar = templateCar;
    }


}
