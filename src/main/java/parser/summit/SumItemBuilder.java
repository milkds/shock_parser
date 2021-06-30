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
import java.util.*;

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
        getKitContent(doc);
        getImages(doc);
     //   getVideos(doc);
       // getBrand(doc);
       // getPart(doc);
        getProperties(doc);
        if (fitPages.size()!=0){
            List<SumFitment> fits = new SumFitBuilder(fitPages).getFits(result);
            if (fits.size()>0){
                this.result.setFitments(fits);
            }
        }

        return result;
    }

    private void getKitContent(Document doc) {
        Element kitElMark = doc.getElementById("kitcombocontents");
        if (kitElMark==null){
            return;
        }
        Element kitEl = doc.getElementsByClass("kit-combo-content-container").first();
        String kitElHtml = kitEl.html();
        result.setKitContents(kitElHtml);
    }

    private void getProperties(Document doc) {
        Element propElsAll = doc.getElementsByClass("attribute-container").first();
        Map<String, String> attributes = getItemAttributes(propElsAll);
        List<SumItemAttribute> itemAttributes = new ArrayList<>();
        attributes.forEach((k,v)->{
            switch (k){
                case "Brand:": result.setBrand(v); break;
                case "Manufacturer's Part Number:": result.setPartNo(v); break;
                case "Part Type:": result.setItemType(v); break;
                default: itemAttributes.add(new SumItemAttribute(k,v));
            }
        });
        result.setAttributes(itemAttributes);
        itemAttributes.forEach(att->{
            att.setItem(result);
        });

        Element descEl = doc.getElementsByClass("part-detail-description").first();
        if (descEl==null){
            result.setDescription("");
            return;
        }
        String descTxt = descEl.text();
        result.setDescription(descTxt);
    }

    private Map<String, String> getItemAttributes(Element propElsAll) {
        Map<String, String> result = new HashMap<>();
        Elements propEls = propElsAll.getElementsByClass("small-12");
        String name = "";
       for (Element propEl: propEls){
            String txt = propEl.text().trim();
            if (name.length()==0){
                name = txt;
            }
            else {
                result.put(name, txt);
                name = "";
            }
       }
        return result;
    }

    /*private List<SumItemAttribute> getItemAttributes(Elements propEls) {
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
    }*/

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

    private void getImages(Document doc){
        Element allPicEL = doc.getElementsByClass("part-detail-image-thumbs").first();
        if (allPicEL==null){
            result.setPicUrls("");
            return;
        }
        Elements picEls = allPicEL.getElementsByTag("li");
        if (picEls.size()==0){
            result.setPicUrls("");
            return;
        }
        String mainPic = getPicUrlFromEl(picEls.get(0));
        if (mainPic==null){
            result.setPicUrls("");
            return;
        }
        mainPic = convertPicToLarge("small", "_s", mainPic)+"_0";
        if (picEls.size()==1){
            result.setPicUrls(mainPic);
            return;
        }
        Set<String> pics = new HashSet<>();
        for (int i = 1; i < picEls.size(); i++) {
            String url = getPicUrlFromEl(picEls.get(i));
            if (url==null){
                continue;
            }
            pics.add(convertPicToLarge("small","_s",url));
        }
        if (allPicEL.getElementsByClass("thumbnail-count").size()!=0){
          Elements kitEls = doc.getElementsByClass("kit-combo-content-container");
          if (kitEls.size()==1){
              Elements imgEls = kitEls.get(0).getElementsByTag("img");
              imgEls.forEach(imgEl->{
                  String url = imgEl.attr("src");
                  pics.add(convertPicToLarge("norm", "_m", url));
              });
          }
        }
        String allPics = buildPicField(mainPic, pics);
        result.setPicUrls(allPics);
    }

    private String buildPicField(String mainPic, Set<String> pics) {
        StringBuilder sb = new StringBuilder(mainPic);
        int counter = 1;
        for (String pic: pics){
            sb.append("div");
            sb.append(pic);
            sb.append("_").append(counter);
            counter++;
        }

        return sb.toString();
    }

    private String getPicUrlFromEl(Element element) {
        Element imgEl = element.getElementsByTag("img").first();
        Element videoeL = element.getElementsByClass("media-thumbs-video").first();
        if (videoeL!=null){
            return null;
        }
        return imgEl.attr("src");
    }

    private String convertPicToLarge(String size, String prefix, String url) {
        String result = url.replace(size, "large");
        result = result.replace(prefix, "_xl");

        return result;
    }

    private void getImages2(Document doc) {
        StringBuilder urlsCollector = new StringBuilder();
        Element allPicEL = doc.getElementsByClass("part-detail-image-thumbs").first();
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
            /*String alt = imgEl.attr("alt");
            boolean actual = false;
            if (alt.endsWith("False")){
                actual = true;
            }
            url = url+"_"+actual;*/
            counter++;
            urlsCollector.append(url);
            urlsCollector.append("div");
        }
        String urls = urlsCollector.toString();
        if (allPicEL.getElementsByClass("thumbnail-count").size()!=0){
            urls = getMoreImages(urls, picEls);
        }
        else {
            urls = urls.substring(0,urls.length()-3);
        }
        result.setPicUrls(urls);
    }

    private String getMoreImages(String urls, Elements picEls) {
        String kits = result.getKitContents();
        if (kits==null){
            return urls;
        }
        //https://static.summitracing.com/global/images/prod/small/emu-2418_s.jpg
       // https://static.summitracing.com/global/images/prod/norm/emu-2418_m.jpg


        return "";
    }

    private void getShortDesc(Document doc) {
        Element shDesEl = doc.getElementsByClass("item-description").first();
      //  shDesEl = shDesEl.getElementsByClass("suggestBeatAPrice").first();
     //   shDesEl = shDesEl.getElementsByClass("description").first();
        String txt = shDesEl.text();
        txt = txt.replace("See More Specifications","").trim();
        result.setShortDesc(txt);
    }

    private void getPrice(Document doc) {
        Element priceEl = doc.getElementsByClass("price").first();
        String priceT = "";
        try{
           priceT = priceEl.text();
       }
       catch (NullPointerException e){
           result.setPrice(new BigDecimal(0));
           return;
       }
        priceT = priceT.replace("$", "");
        priceT = priceT.replace(",", "");
        BigDecimal price = new BigDecimal(Double.parseDouble(priceT));
        result.setPrice(price);
    }

    private void getTitle(Document doc) {
        Element titleEl = doc.getElementsByAttributeValueStarting("class", "part-detail-title").first();
        /*if (titleEl==null){
            System.out.println(doc.toString());
            System.exit(1);
        }*/
       // titleEl = titleEl.getElementsByClass("title").first();
        result.setTitle(titleEl.text());
        logger.debug("Building " + result.getTitle());
    }
}
