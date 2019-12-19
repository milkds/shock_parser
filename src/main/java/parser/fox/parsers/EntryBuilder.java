package parser.fox.parsers;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import parser.fox.entities.TableEntry;

import java.util.List;

public class EntryBuilder {

    public TableEntry buildEntry(WebElement rowElement){
        TableEntry result = new TableEntry();
        List<WebElement> tableCols = rowElement.findElements(By.tagName("td"));
        setWeblink(tableCols, result);
        setTitle(tableCols, result);
        setFitNote(tableCols, result);
        setPosition(tableCols, result);
        setDrive(tableCols, result);
        setLift(tableCols, result);
        setPartNo(tableCols, result);
        setPrice(tableCols, result);

        return result;
    }

    private void setTitle(List<WebElement> tableCols, TableEntry result) {
        WebElement linkEl = tableCols.get(0);
        linkEl = linkEl.findElement(By.tagName("a"));
        result.setTitle(linkEl.getText());
    }

    private void setPrice(List<WebElement> tableCols, TableEntry result) {
        WebElement priceEl = tableCols.get(5);
        result.setPrice(priceEl.getText());
    }

    private void setPartNo(List<WebElement> tableCols, TableEntry result) {
        WebElement partEl = tableCols.get(4);
        result.setPartNo(partEl.getText());
    }

    private void setLift(List<WebElement> tableCols, TableEntry result) {
        WebElement liftEl = tableCols.get(3);
        result.setLift(liftEl.getText());
    }

    private void setDrive(List<WebElement> tableCols, TableEntry result) {
        WebElement driveEl = tableCols.get(2);
        String drive = driveEl.getText();
       /* if (drive.equals("4WD")){
            drive = "ALL";
        }*/
        result.setDrive(drive);
    }

    private void setPosition(List<WebElement> tableCols, TableEntry result) {
        WebElement posEl = tableCols.get(1);
        result.setPosition(posEl.getText());
    }

    private void setFitNote(List<WebElement> tableCols, TableEntry result) {
        WebElement noteEl = tableCols.get(0);
        try {
            noteEl = noteEl.findElement(By.tagName("small"));
        }
        catch (NoSuchElementException e){
            result.setFitNote("N/A");
            return;
        }
        result.setFitNote(noteEl.getText());
    }

    private void setWeblink(List<WebElement> tableCols, TableEntry result) {
        WebElement linkEl = tableCols.get(0);
        linkEl = linkEl.findElement(By.tagName("a"));
        result.setWebLink(linkEl.getAttribute("href"));
    }
}
