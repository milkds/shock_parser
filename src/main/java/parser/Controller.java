package parser;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import parser.summit.SumCatSurfer;
import parser.utils.BasicUtils;
import parser.utils.RunUtil;
import parser.utils.SileniumUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    public static void main(String[] args) throws InterruptedException {
        try(FileWriter fw = new FileWriter("src\\main\\resources\\output.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            List<String> inputLines = BasicUtils.getInputInfo();
            inputLines.forEach(line->{
               out.println(StringUtils.substringBetween(line, "<loc>", "</loc>"));
            });
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }

    }

    /*public static void browseSumCats(){
        List<String> catUrls = BasicUtils.getInputInfo();
        List<String> finalUrls = new ArrayList<>();
        catUrls.forEach(url->{
            url = StringUtils.substringBefore(url, "?fr");
            url = url + "?PageSize=100&SortBy=Default&SortOrder=Ascending&autoview=SKU&fr=part-type&ar=1&page=1";
            finalUrls.add(url);
        });
        new SumCatSurfer().printPartsFromCategory(catUrls);
    }*/



}
