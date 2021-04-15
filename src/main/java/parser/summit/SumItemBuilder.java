package parser.summit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import parser.summit.entities.SumFitment;
import parser.summit.entities.SumItem;
import parser.summit.entities.SumItemAttribute;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class SumItemBuilder {
    private String mainPage;
    private List<String> fitPages;
    private SumItem result;
    private static final Logger logger = LogManager.getLogger(SumItemBuilder.class.getName());

    SumItemBuilder(String mainPage, List<String> fitPages) {
        this.mainPage = mainPage;
        this.fitPages = fitPages;
    }

    SumItem buildItem() {
        result = new SumItem();
        Document doc = Jsoup.parse(mainPage);
        getTitle(doc);
        getPrice(doc);
        getShortDesc(doc);
        getImages(doc);
        getVideos(doc);
        getBrand(doc);
        getPart(doc);
        getProperties(doc);
        if (fitPages.size()!=0){
            List<SumFitment> fits = new SumFitBuilder(fitPages).getFits(result);
            if (fits.size()>0){
                this.result.setFitments(fits);
            }
        }

        return result;
    }

    private void getProperties(Document doc) {
        Element propElsAll = doc.getElementById("part-tab-info");
        Element propElsPairs = propElsAll.getElementsByClass("overview").first();
        Elements propEls = propElsPairs.getElementsByTag("p");

        List<SumItemAttribute> attributes = getItemAttributes(propEls);
        this.result.setAttributes(attributes);

        Element descEl = propElsAll.getElementsByClass("overview-description").first();
        String descTxt = descEl.text().trim();
        this.result.setDescription(descTxt);
    }

    private List<SumItemAttribute> getItemAttributes(Elements propEls) {
        List<SumItemAttribute> result = new ArrayList<>();
        propEls.forEach(element -> {
            Elements spanEls = element.getElementsByTag("span");
            String name = spanEls.get(0).text();
            String value = spanEls.get(1).text();
            if (name.equals("Part Type:")){
                this.result.setItemType(value);
            }
            else {
                SumItemAttribute att = new SumItemAttribute(name, value);
                att.setItem(this.result);
                result.add(att);
            }
        });

        return result;
    }

    private void getPart(Document doc) {
        Element propEl = doc.getElementById("part-tab-info");
        Element partEl = propEl.getElementsByAttributeValue("itemprop", "mpn").first();
        result.setPartNo(partEl.text());
    }

    private void getBrand(Document doc) {
        Element propEl = doc.getElementById("part-tab-info");
        Element brandEl = propEl.getElementsByAttributeValue("itemprop", "brand").first();
        result.setBrand(brandEl.text());
    }

    private void getVideos(Document doc) {
        StringBuilder urlsCollector = new StringBuilder();
        Element allVidEl = doc.getElementsByClass("media-thumbnails-video").first();
        if (allVidEl==null){
            result.setVideoUrls("");
            return;
        }
        Elements vidEls = allVidEl.getElementsByTag("li");
        for (Element vidEl : vidEls) {
            String url = vidEl.getElementsByTag("a").first().attr("data-video");
            urlsCollector.append(url);
            urlsCollector.append("div");
        }
        String urls = urlsCollector.toString();
        urls = urls.substring(0, urls.length()-3);
        urls = urls.replaceAll("VIDEO:", "");
        result.setVideoUrls(urls);
    }

    private void getImages(Document doc) {
        StringBuilder urlsCollector = new StringBuilder();
        Element allPicEL = doc.getElementsByClass("media-thumbnails-images").first();
        if (allPicEL==null){
            result.setPicUrls("");
            return;
        }
        Elements picEls = allPicEL.getElementsByTag("li");
        if (picEls.size()==0){
            result.setPicUrls("");
            return;
        }
        int counter = 0;
        for (Element picEl : picEls) {
            Element imgEl = picEl.getElementsByTag("img").first();
            if (imgEl==null){
                continue;
            }
            String url = imgEl.attr("src");
            url = url.replace("small", "xlarge");
            url = url.replace("_s.jpg", "_xl.jpg_" + counter);
            String alt = imgEl.attr("alt");
            boolean actual = false;
            if (alt.endsWith("False")){
                actual = true;
            }
            url = url+"_"+actual;
            counter++;
            urlsCollector.append(url);
            urlsCollector.append("div");
        }
        String urls = urlsCollector.toString();
        if (allPicEL.getElementsByClass("moreimage").size()!=0){
            urls = urls + "_HMI";
        }
        else {
            urls = urls.substring(0,urls.length()-3);
        }
        result.setPicUrls(urls);
    }

    private void getShortDesc(Document doc) {
        Element shDesEl = doc.getElementsByClass("part-description").first();
        shDesEl = shDesEl.getElementsByClass("suggestBeatAPrice").first();
        shDesEl = shDesEl.getElementsByClass("description").first();
        result.setShortDesc(shDesEl.text());
    }

    private void getPrice(Document doc) {
        Element priceEl = doc.getElementsByClass("price").first();
        String priceT = priceEl.text();
        priceT = priceT.replace("$", "");
        priceT = priceT.replace(",", "");
        BigDecimal price = new BigDecimal(Double.parseDouble(priceT));
        result.setPrice(price);
    }

    private void getTitle(Document doc) {
        Element titleEl = doc.getElementsByAttributeValueStarting("class", "detail-title").first();
        /*if (titleEl==null){
            System.out.println(doc.toString());
            System.exit(1);
        }*/
        titleEl = titleEl.getElementsByClass("title").first();
        result.setTitle(titleEl.text());
        logger.debug("Building " + result.getTitle());
    }
}
