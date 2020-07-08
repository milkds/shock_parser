package parser.eibach;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import parser.eibach.entiities.EibCar;
import parser.eibach.entiities.EibItem;
import parser.eibach.entiities.MetBlock;
import parser.eibach.entiities.StdBlock;
import parser.utils.HibernateUtil;

import java.math.BigDecimal;

public class ItemBuilder {

    private static final Logger logger = LogManager.getLogger(ItemBuilder.class.getName());
    private Document doc;
    private String url;

    public EibItem buildItem(){
        logger.info("checking " + url);
        if (url.equals("https://eibach.com/us/i-25546-faux-product.html")){
            return null;
        }
        EibItem result = new EibItem();
        setProdInfoFields(result);
        if (result.getTitle()==null){
            return null;
        }
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
        if (hasFit(fitBlockEl)){
            setCar(fitBlockEl, result);
        }
        setSpringParams(fitBlockEl, result);
        if (fitBlockEl==null){
            setEndLinkFields(result);
        }
    }

    private void setEndLinkFields(EibItem result) {
        Element blockEl = doc.getElementsByClass("eps-values").first();
        if (blockEl==null){
            return;
        }
        result.setBoltDiam(blockEl.getElementsByClass("tdiam").first().text());
        try {
            result.setElLength(blockEl.getElementsByClass("tleng").first().text());
        }
        catch (NullPointerException ignored){}
        try {
            result.setElWidth(blockEl.getElementsByClass("twidth").first().text());
        }
        catch (NullPointerException ignored){}
        try {
            result.setDuty(blockEl.getElementsByClass("tduty").first().text());
        }
        catch (NullPointerException ignored){}

        result.setQuantity(blockEl.getElementsByClass("tqty").first().text());
    }

    private boolean hasFit(Element fitBlockEl) {
        if (fitBlockEl == null){
            return false;
        }
        Element carBlockEl = fitBlockEl.getElementsByClass("eps-values").first();
        if (carBlockEl!=null){
            return true;
        }
        else {
            carBlockEl = fitBlockEl.getElementsByClass("ems-values").first();
        }

        return carBlockEl != null;
    }

    private void setSpringParams(Element fitBlockEl, EibItem result) {
        if (fitBlockEl==null){
            return;
        }
        Element standardBlock = fitBlockEl.getElementsByClass("standard").first();
        Element metricBlock = fitBlockEl.getElementsByClass("metric").first();
        if (standardBlock==null&&metricBlock==null){
            return;
        }
        if (standardBlock!=null){
            setStandardBlock(standardBlock, result);
        }
        if (metricBlock!=null){
            setMetricBlock(metricBlock, result);
        }
    }

    private void setMetricBlock(Element metricBlockEl, EibItem result) {
        MetBlock metblock = new MetBlock();
        Element curEl = metricBlockEl.getElementsByClass("len-mm").first();{
            if (curEl!=null){
                metblock.setLength_mm(curEl.text());
            }
        }
        curEl = metricBlockEl.getElementsByClass("dia-mm").first();{
            if (curEl!=null){
                metblock.setDiameter_mm(curEl.text());
            }
        }
         curEl = metricBlockEl.getElementsByClass("rate-lbs-in").first();{
            if (curEl!=null){
                metblock.setRate_lbs_to_in(curEl.text());
            }
        }
         curEl = metricBlockEl.getElementsByClass("rate-kg-mm").first();{
            if (curEl!=null){
                metblock.setRate_kg_to_mm(curEl.text());
            }
        }
         curEl = metricBlockEl.getElementsByClass("rate-n-mm").first();{
            if (curEl!=null){
                metblock.setRate_N_to_mm(curEl.text());
            }
        }
         curEl = metricBlockEl.getElementsByClass("bh-mm").first();{
            if (curEl!=null){
                metblock.setBlock_height_mm(curEl.text());
            }
        }
         curEl = metricBlockEl.getElementsByClass("trav-mm").first();{
            if (curEl!=null){
                metblock.setTravel_mm(curEl.text());
            }
        }
         curEl = metricBlockEl.getElementsByClass("bl-n").first();{
            if (curEl!=null){
                metblock.setBlock_load_N(curEl.text());
            }
        }
         curEl = metricBlockEl.getElementsByClass("weight-kgs").first();{
            if (curEl!=null){
                metblock.setWeight_kgs(curEl.text());
            }
        }
       result.setMetBlock(metblock);
        metblock.setItem(result);
    }

    private void setStandardBlock(Element stBlockEl, EibItem result) {
        StdBlock block = new StdBlock();
        Element curEl = stBlockEl.getElementsByClass("len-in").first();{
            if (curEl!=null){
                block.setLength_in(curEl.text());
            }
        }
        curEl = stBlockEl.getElementsByClass("dia-in").first();{
            if (curEl!=null){
                block.setDiameter_in(curEl.text());
            }
        }
        curEl = stBlockEl.getElementsByClass("rate-lbs-in").first();{
            if (curEl!=null){
                block.setRate_lbs_to_in(curEl.text());
            }
        }
         curEl = stBlockEl.getElementsByClass("rate-kg-mm").first();{
            if (curEl!=null){
                block.setRate_kg_to_mm(curEl.text());
            }
        }
         curEl = stBlockEl.getElementsByClass("bh-in").first();{
            if (curEl!=null){
                block.setBlock_height_in(curEl.text());
            }
        }
         curEl = stBlockEl.getElementsByClass("trav-in").first();{
            if (curEl!=null){
                block.setTravel_in(curEl.text());
            }
        }
         curEl = stBlockEl.getElementsByClass("bl-lbs").first();{
            if (curEl!=null){
                block.setBlock_load_lbs(curEl.text());
            }
        }
         curEl = stBlockEl.getElementsByClass("weight-lbs").first();{
            if (curEl!=null){
                block.setWeight_lbs(curEl.text());
            }
        }
       result.setStdBlock(block);
        block.setItem(result);
    }

    private void setCar(Element fitBlockEl, EibItem result) {
        Element carBlockEl = fitBlockEl.getElementsByClass("eps-values").first();
        if (carBlockEl==null){
            carBlockEl = fitBlockEl.getElementsByClass("ems-values").first();
        }
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
            eibCar.setRate(carBlockEl.getElementsByClass("trate").first().text());
        }
        catch (NullPointerException ignored){}
        try {
            eibCar.setNote(carBlockEl.getElementsByClass("tcomment").first().text());
        }
        catch (NullPointerException ignored){}
    }

    private void setDesc(Element fitBlockEl, EibItem result) {
        if (fitBlockEl==null){
            result.setDesc("NO DESCRIPTION");
            return;
        }
        Element descEl = fitBlockEl.getElementsByTag("ul").first();
        if (descEl==null){
            result.setDesc("NO DESCRIPTION");
            return;
        }
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
        if (itemPicEl==null){
            result.setImgLink("NO PIC AVAILABLE");
        }
        else {
            result.setImgLink(itemPicEl.attr("src"));
        }
    }

    private void setPricingFields(EibItem result) {
        setPrices(result);
        setStock(result);
        setShipping(result);
    }

    private void setShipping(EibItem result) {
        Element shipEl = doc.getElementsByAttributeValueContaining("class", "wsm-cat-ship-remarks").first();
        if (shipEl == null){
            result.setShipment("NO SHIPMENT INFO AVAILABLE");
        }
        else {
            result.setShipment(shipEl.text());
        }
    }

    private void setStock(EibItem result) {
        Element stockEl = doc.getElementsByAttributeValueContaining("class", "wsm-cat-avail-remarks-value").first();
        if (stockEl==null){
            result.setStock("NO STOCK INFO");
        }
        else {
            result.setStock(stockEl.text());
        }
    }

    private void setPrices(EibItem result) {
        Element oldPriceEl = doc.getElementsByAttributeValue("class", "wsm-cat-price-was-value wsmjs-product-price").first();
        BigDecimal oldPrice = null;
        if (oldPriceEl==null){
            oldPrice = new BigDecimal(0);
        }
        else {
            oldPrice = getPriceFromString(oldPriceEl.text());
        }
        Element newPriceEl = doc.getElementsByAttributeValue("itemprop", "price").first();
        BigDecimal newPrice = null;
        if (newPriceEl==null){
            newPrice = new BigDecimal(0);
        }
        else {
            newPrice = getPriceFromString(newPriceEl.text());
        }
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
        if (prodInfoEl == null) {
            return;
        }
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
        if (partEl==null){
            return;
        }
        result.setPartNo(partEl.text());
    }

    public ItemBuilder(Document doc, String url) {
        this.doc = doc;
        this.url = url;
    }
}
