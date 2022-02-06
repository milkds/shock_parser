package parser.summit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import parser.summit.entities.SumItem;
import parser.summit.entities.SumPage;
import parser.utils.BasicUtils;
import parser.utils.HibernateUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SummitController {
    private static final Logger logger = LogManager.getLogger(SummitController.class.getName());


    public void saveItemsToDB(String brand){
        Map<String, List<String>> pages = new SumPageToDiscUtil(brand).getAllPagesForBrand(); //k = main page, v = fits
        List<SumItem> items = new ArrayList<>();
        int total = pages.entrySet().size();
        int counter = 1;
        for (Map.Entry<String, List<String>> entry : pages.entrySet()) {
            String k = entry.getKey();
            List<String> v = entry.getValue();
            SumItem item = new SumItemBuilder(k, v).buildItem();
            items.add(item);
            logger.info("Built item " + counter + " of total " + total);
            counter++;
        }
        saveItems(items);
    }

    private void saveItems(List<SumItem> items) {
        logger.info("Items Saved");
        Session session = HibernateUtil.getSummitSessionFactory().openSession();
        items = SummitService.skipSavedItems(items, session);
        items.forEach(item->{
            new SummitService().saveItem(item, session);
        });
        session.close();
        HibernateUtil.shutdown();
    }

    public void checkParseConsistency(String brand){
        Map<String, List<String>> fNames = new SumPageToDiscUtil(brand).getFileNamesForBrand(); //k = itemPageName, v = fits.
        SumParseChecker checker = new SumParseChecker(fNames);
        checker.checkFitFilesExistence(brand);
        checker.checkFitQuantities(brand);
    }

    public void printParts(){
        String doc = getTestDoc();
        List<String> parts = new SummitPageReader(doc).getParts();
    }

    private static List<String> getInputInfo() {
        List<String> result = new ArrayList<>();
        String fileName = "src\\main\\resources\\summit_test.txt";
        try {
            result = Files.readAllLines(Paths.get(fileName),
                    Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String getTestDoc(){
       /* List<String> lines = getInputInfo();
        StringBuilder sb = new StringBuilder();
        lines.forEach(s -> {
            sb.append(s);
            sb.append(System.lineSeparator());
        });

        return sb.toString();*/
       String res = "";
       try {
           res = new HttpReqUtil().getHTMLfromURL("https://www.summitracing.com/search/brand/icon-vehicle-dynamics?PageSize=100&SortBy=Default&SortOrder=Default");
       }
       catch (IOException ignored){}

       return res;
    }

    public void getParts(String url){
        SumPagesGetter pagesGetter = new SumPagesGetter();
        String firstPage = getFirstPage(url, pagesGetter);
        if (firstPage.length()==0){
            logger.error("Couldn't get first page");
            System.exit(1);
        }
        int pageQty = new SummitPageReader(firstPage).getPageQuantity(); // getting quantity of pages to browse
        List<String> parts = browsePages(firstPage, pageQty, url, pagesGetter);
        parts.forEach(System.out::println);


        //https://www.summitracing.com/search/brand/icon-vehicle-dynamics?PageSize=100&SortBy=Default&SortOrder=Default
        //https://www.summitracing.com/search/brand/icon-vehicle-dynamics?PageSize=100&SortBy=Default&SortOrder=Default&page=2
    }

    private List<String> browsePages(String firstPage, int pageQty, String url, SumPagesGetter pagesGetter) {
        List<String> result = new ArrayList<>(new SummitPageReader(firstPage).getParts());
        for (int i = 2; i <=pageQty ; i++) {
            String currentUrl = url+"&page="+i;
            logger.info("Making Pause");
            randomSleep();
            logger.info("Pause finished");
            String page = pagesGetter.getValidPage(currentUrl);
            if (page.length()>0){
                result.addAll(new SummitPageReader(page).getParts());
            }
            else {
                logger.error("couldn't get parts from " + currentUrl);
            }
        }

        return result;
    }

    private void randomSleep() {
        try {
            long rand = (long)(Math.random()*15000);
            rand = rand + 25000;
            logger.debug("Pause " + rand + " millis");
            Thread.sleep(rand);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getFirstPage(String url, SumPagesGetter pagesGetter) {
        return pagesGetter.getValidPage(url);

       /* List<String> lines = getInputInfo();
        StringBuilder sb = new StringBuilder();
        lines.forEach(s -> {
            sb.append(s);
            sb.append(System.lineSeparator());
        });

        return sb.toString();*/
    }

    public void getItemPages(String brand){
        Session sumSession = HibernateUtil.getSummitSessionFactory().openSession();
        List<String> parts = getPartsForParse(sumSession);
        logger.info("Got parts for parse. Total quantity is " + parts.size());
        SumPageToDiscUtil saver = new SumPageToDiscUtil(brand);
        getPartsPages(parts, saver, sumSession, brand);
        sumSession.close();
        HibernateUtil.shutdown();
    }

    private void getPartsPages(List<String> parts, SumPageToDiscUtil saver, Session sumSession, String brand) {
        int counter = 0;
        int total = parts.size();
        boolean hasSavedFits = true;
        SumPagesGetter getter = new SumPagesGetter();
        for (String part: parts){
            String url = "https://www.summitracing.com/int/parts/"+part;
          //  String url = "https://www.summitracing.com/parts/"+part;
            String urlHTML = getter.getValidPageNoRef(url);
            randomSleep();
            if (hasApplications(urlHTML)){
                if (hasSavedFits){
                   int startFit = new SumPageToDiscUtil(brand).getStartFit(part);
                    saveFitPages(getter, url, saver, startFit);
                    hasSavedFits = false;
                }
               else{
                   saveFitPages(getter, url, saver, 2);
                }
            }
            saver.savePage(url, urlHTML);
            counter++;
            SumPage page = new SumPage();
            page.setUrl(url);
            page.setPart(part);
            new SummitService().savePageToDB(page, sumSession);
            logger.info("Saved page " + counter + " of total " + total);
            randomSleep();
        }
    }

    private boolean hasApplications(String urlHTML) {
        Document doc = Jsoup.parse(urlHTML);
        Element el = doc.getElementById("applications");

        return el!=null;
    }

    private void saveFitPages(SumPagesGetter getter, String url, SumPageToDiscUtil saver, int startFit) {
        String appUrl = url+"/applications";
        String appPage = getter.getValidPage(appUrl);
        saver.savePage(appUrl, appPage);
        logger.info("Saved first fit page of " + url);
        if (SummitPageReader.isUniversal(appPage)){
            return;
        }
        int totalResults = SummitPageReader.getTotalFitQty(appPage);
        if (totalResults<21){
            return;
        }
        int totalPages = getTotalPages(totalResults);
        logger.info("Total fit pages "+ totalPages);
        for (int i = startFit; i <= totalPages; i++) {
            randomSleep();
            String curUrl = appUrl+"?page="+i;
            String currentPage = getter.getValidPage(curUrl);
            saver.savePage(curUrl, currentPage);
            logger.info("Saved fit page " + i + " of total " + totalPages);
        }
    }

    private int getTotalPages(int totalResults) {
        int fullPages = totalResults/20;
        int fullResults = fullPages*20;
        if (totalResults-fullResults==0){
            return fullPages;
        }
        return fullPages+1;
    }

    private List<String> getPartsForParse(Session sumSession) {
        List<String> result = BasicUtils.getInputInfo();
        List<String> parsedParts = SummitService.getParsedParts(sumSession);
        result.removeAll(parsedParts);

        return result;
    }


}
