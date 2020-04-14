package parser.eibach;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import parser.eibach.entiities.EibCar;
import parser.eibach.entiities.EibItem;

import java.math.BigDecimal;

public class ItemBuilder {

    private static final Logger logger = LogManager.getLogger(ItemBuilder.class.getName());
    private Document doc;
    private String url;

    public EibItem buildItem(){
        logger.info("checking " + url);
        EibItem result = new EibItem();
        setProdInfoFields(result);
        setPricingFields(result);
        setItemPic(result);
        setFitFields(result);
        setUrl(result);
        logger.info("Built item " + result);

        return result;
    }

    private void setFitFields(EibItem result) {
        Element fitBlockEl = doc.getElementById("wsm-prod-tab-decrip");
        setDesc(fitBlockEl, result);
        setCar(fitBlockEl, result);
    }

    private void setCar(Element fitBlockEl, EibItem result) {
        Element carBlockEl = fitBlockEl.getElementsByClass("eps-values").first();
        EibCar eibCar = new EibCar();
        eibCar.setYear(carBlockEl.getElementsByClass("tyear").first().text());
        eibCar.setMake(carBlockEl.getElementsByClass("tmake").first().text());
        eibCar.setModel(carBlockEl.getElementsByClass("tmodel").first().text());

        setCarAdditionalFields(eibCar, carBlockEl);
        result.setCar(eibCar);
        eibCar.setItem(result);
    }

    private void setCarAdditionalFields(EibCar eibCar, Element carBlockEl) {
        try {
            eibCar.setSubmodel(carBlockEl.getElementsByClass("tsub").first().text());
        }
        catch (NullPointerException ignored){}
        try {
            eibCar.setFront(carBlockEl.getElementsByClass("tfront").first().text());
        }
        catch (NullPointerException ignored){}
        try {
            eibCar.setRear(carBlockEl.getElementsByClass("trear").first().text());
        }
        catch (NullPointerException ignored){}
        try {
            eibCar.setNote(carBlockEl.getElementsByClass("tcomment").first().text());
        }
        catch (NullPointerException ignored){}
    }

    private void setDesc(Element fitBlockEl, EibItem result) {
        Element descEl = fitBlockEl.getElementsByTag("ul").first();
        Elements descEls = descEl.getElementsByTag("li");
        StringBuilder descBuilder = new StringBuilder();
        descEls.forEach(dEl->{
            String desc = dEl.text();
            if (desc.length()>0){
                descBuilder.append(dEl.text());
                descBuilder.append(System.lineSeparator());
            }
        });
        result.setDesc(descBuilder.toString());
    }


    private void setItemPic(EibItem result) {
        Element itemPicEl = doc.getElementById("wsm-prod-rotate-image");
        itemPicEl = itemPicEl.getElementsByTag("img").first();
        result.setImgLink(itemPicEl.attr("src"));
    }

    private void setPricingFields(EibItem result) {
        setPrices(result);
        setStock(result);
        setShipping(result);
    }

    private void setShipping(EibItem result) {
        Element shipEl = doc.getElementsByAttributeValueContaining("class", "wsm-cat-ship-remarks").first();
        result.setShipment(shipEl.text());
    }

    private void setStock(EibItem result) {
        Element stockEl = doc.getElementsByAttributeValueContaining("class", "wsm-cat-avail-remarks-value").first();
        result.setStock(stockEl.text());
    }

    private void setPrices(EibItem result) {
        Element oldPriceEl = doc.getElementsByAttributeValue("class", "wsm-cat-price-was-value wsmjs-product-price").first();
        BigDecimal oldPrice = getPriceFromString(oldPriceEl.text());
        Element newPriceEl = doc.getElementsByAttributeValue("itemprop", "price").first();
        BigDecimal newPrice = getPriceFromString(newPriceEl.text());
        result.setOldPrice(oldPrice);
        result.setNewPrice(newPrice);
    }

    private BigDecimal getPriceFromString(String text) {
        String price = text.replace("$", "");
        price = price.replaceAll(",", "");
        BigDecimal result = new BigDecimal(price);

        return result;
    }

    private void setUrl(EibItem result) {
        result.setUrl(url);
    }

    private void setProdInfoFields(EibItem result) {
        Element prodInfoEl = doc.getElementById("wsm-prod-info");
        setTitle(prodInfoEl, result);
        setFitTitle(prodInfoEl, result);
        setPartNo(prodInfoEl, result);
        setRideHeight(prodInfoEl, result);
        setProdNote(prodInfoEl, result);
    }

    private void setProdNote(Element prodInfoEl, EibItem result) {
        Element noteEl = prodInfoEl.getElementsByClass("note-bx").first();
        if (noteEl!=null){
            result.setProdNote(noteEl.text());
        }
    }

    private void setRideHeight(Element prodInfoEl, EibItem result) {
        Element heightEl = prodInfoEl.getElementsByClass("ride-height").first();
        if (heightEl==null){
            return;
        }
        result.setRideHeight(heightEl.text());

    }

    private void setTitle(Element prodInfoEl, EibItem result) {
        Element titleEl = prodInfoEl.getElementsByClass("wsm-prod-title").first();
        String fullTitle = titleEl.text();
        result.setTitle(fullTitle);
    }

    private void setFitTitle(Element prodInfoEl, EibItem result) {
        Element fitTitleEl = prodInfoEl.getElementById("product-fitment");
        if (fitTitleEl!=null){
            result.setFitTitle(fitTitleEl.text());
        }
    }

    private void setPartNo(Element prodInfoEl, EibItem result) {
        Element partEl = prodInfoEl.getElementsByClass("wsm-prod-sku").first();
        result.setPartNo(partEl.text());
    }

    public ItemBuilder(Document doc, String url) {
        this.doc = doc;
        this.url = url;
    }
}
