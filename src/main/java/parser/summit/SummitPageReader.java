package parser.summit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class SummitPageReader {

    private String page;
    private static final Logger logger = LogManager.getLogger(SummitPageReader.class.getName());

    public SummitPageReader(String page) {
        this.page = page;
    }

    public static boolean isDistil(String pageCode) {
        Document doc = Jsoup.parse(pageCode);

        Element distilEL = doc.getElementById("distilIdentificationBlock");

        return distilEL != null;
    }

    static boolean isUniversal(String appPage) {
        Document doc = Jsoup.parse(appPage);
        Elements results = getResultsFromAppPage(doc);
        if (results==null){
            return true;
        }
        Element first = results.first();
        String uniTxt = getUniTxt(first);
        logger.debug("uniText "+uniTxt);
        if (uniTxt.contains("Universal")){
            return uniTxt.contains("Yes");
        }

        return false;
    }

    private static String getUniTxt(Element first) {
        Elements els = null;
        try {
             els = first.getElementsByClass("small-12");
        }
       catch (NullPointerException e){
            return "";
       }

        return els.get(0).text()+" " + els.get(1).text();
    }

    private static Elements getResultsFromAppPage(Document doc) {
      //  Element resultEl = doc.getElementsByClass("application-header").first();
        Elements results = null;
        try {
            results = doc.getElementsByClass("attribute-container");
       }
        catch (NullPointerException e){
            logger.info("no applications tab for current item");
            return results;
        }
        logger.debug("total results " + results.size());

        return results;
    }

    static int getTotalFitQty(String appPage) {
        Document doc = Jsoup.parse(appPage);
        Element totalEl = doc.getElementsByClass("application-header").first();
        String text = totalEl.text();
        String totalQty = StringUtils.substringAfter(text,"Results Found: ").trim();
        //totalQty = totalQty.replaceAll(",", "");
        logger.info("total fitment quantity is " + totalQty);

        return Integer.parseInt(totalQty);
    }

    static int getCurPageStartItem(String page) {
        Document doc = Jsoup.parse(page);
        Element qtyElement = doc.getElementsByClass("results-of-total").first();
        String text = qtyElement.text();
//        logger.info("Quantity text " + text);
        String firstItem = StringUtils.substringBefore(text, " -").trim();
        firstItem = firstItem.replace("Results ", "");

        return Integer.parseInt(firstItem);
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public List<String> getParts() {
        List<String> result = new ArrayList<>();
        Document doc = Jsoup.parse(page);
       try {
           Element partBlockEl = doc.getElementById("sResultsComp");
           Elements partEls = partBlockEl.getElementsByAttribute("data-prodid");
           partEls.forEach(el->{
               result.add(el.attr("data-prodid"));
           });
       }
       catch (NullPointerException e){
           System.out.println(page);
       }

        return result;
    }

    int getPageQuantity() {
        Document doc = Jsoup.parse(page);
     //   System.out.println(page.toString());
        Element pageEl = doc.getElementById("SingleParts");
        pageEl = pageEl.getElementsByAttributeValueStarting("class", "results-list-top-bar").first();
        pageEl = pageEl.getElementsByTag("p").first();

        String text = pageEl.text();
        String qty = StringUtils.substringAfter(text,"of ");
        int items = Integer.parseInt(qty);
        int result = items/100;
        if (items-(result*100)!=0){
            result = result+1;
        }

        return result;
    }

    String getFirstScriptName() {
        Document doc = Jsoup.parse(page);
      //  Element scritEL = doc.getElementsByAttribute("src").first();
        Element scritEL = doc.getElementsByAttributeValueStarting("src","/sredstlcp").first();

        return scritEL.attr("src");
    }

    String getHeadScriptName() {
        Document doc = Jsoup.parse(page);
        Element headEl = doc.getElementsByTag("head").first();
        Elements scriptEls = headEl.getElementsByTag("script");
        for (Element el: scriptEls){
            Element srcEl = el.getElementsByAttribute("src").first();
            if (srcEl!=null){
                return srcEl.attr("src");
            }
        }

        return "";
    }
}
