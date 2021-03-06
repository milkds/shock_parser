package parser.summit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Map;

class SumParseChecker {
    private Map<String, List<String>> fNames;
    private static final Logger logger = LogManager.getLogger(SumParseChecker.class.getName());

    SumParseChecker(Map<String, List<String>> fNames) {
        this.fNames = fNames;
    }

    void checkFitFilesExistence(String brand) {
        fNames.forEach((k,v)->{
            if (v.size()==0){
                if (fitsExpected(k, brand)){
                    logger.error("No fit page for " + k);
                }
            }
        });

        logger.info("Fit file existence check complete");
    }

    private boolean fitsExpected(String fName, String brand) {
        String page = new SumPageToDiscUtil(brand).getPageText(fName);
        Document doc = Jsoup.parse(page);
        Element el = doc.getElementById("applications");

        return el!=null;
    }

    void checkFitQuantities(String brand) {
        fNames.forEach((k,v)->{
            String firstFitPageName = getFirstFitName(v);
            logger.info("checking " + buildPageURL(0, firstFitPageName));
            if (firstFitPageName.length()==0){
                return;
            }
            String fit1Page = new SumPageToDiscUtil(brand).getPageText(firstFitPageName);
            if (SummitPageReader.isUniversal(fit1Page)){
                return;
            }
            int totalFitPages = getTotalFitPages(fit1Page);
            if (totalFitPages==v.size()){
                return;
            }
            printMissingPagesUrls(brand, v);
        });
    }

    private void printMissingPagesUrls(String brand, List<String> fitNames) {
        Map<String, String> nameContentMap = new SumPageToDiscUtil(brand).getNameContentMap(fitNames);
        String firstFitName = getFirstFitName(fitNames);
        //first fit will be always present, as without it we can't
        int totalFitPages = getTotalFitPages(nameContentMap.get(firstFitName));
        if (totalFitPages==fitNames.size()){
            return;
        }
        printMissingPages(nameContentMap, firstFitName, totalFitPages);


        //continue here
    }

    //will be always 2+ pages in map.
    private void printMissingPages(Map<String, String> nameContentMap, String firstFitName, int totalFitPages) {
        int counter = 21;
        for (int i = 2; i <= totalFitPages; i++) {
            boolean pageExists = false;
            for (Map.Entry<String, String> entry: nameContentMap.entrySet()){
                int curPageStartItem = SummitPageReader.getCurPageStartItem(entry.getValue());
                if (curPageStartItem==counter){
                    pageExists = true;
                    counter = counter+20;
                    break;
                }
            }
            if (!pageExists){
                logger.info("No page");
                String url = buildPageURL(i, firstFitName);
                System.out.println(url);
            }
        }
    }

    private String buildPageURL(int i, String firstFitName) {
        StringBuilder builder = new StringBuilder("https://www.summitracing.com/int/parts/");
        String part = StringUtils.substringBefore(firstFitName, "_fit1");
        builder.append(part);
        builder.append("/applications?page=");
        builder.append(i);

        return builder.toString();
    }

    private int getTotalFitPages(String fit1Page) {
       int totalFits = SummitPageReader.getTotalFitQty(fit1Page);
       if (totalFits<=20){
           return 1;
       }
       int totalFullPages = totalFits/20;
       int fullFits = totalFullPages*20;
       if (totalFits==fullFits){
           return totalFullPages;
       }

       return totalFullPages+1;
    }

    private String getFirstFitName(List<String> v) {
        for (String fitName : v) {
            if (fitName.contains("fit1.txt")) {
                return fitName;
            }
        }

        //logger.error("No fit1 file name ");

        return "";
    }
}
