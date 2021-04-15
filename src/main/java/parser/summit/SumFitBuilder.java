package parser.summit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import parser.summit.entities.SumFitAttribute;
import parser.summit.entities.SumFitment;
import parser.summit.entities.SumItem;

import java.util.ArrayList;
import java.util.List;

class SumFitBuilder {
    private List<String> fitPages;
    private static final Logger logger = LogManager.getLogger(SumFitBuilder.class.getName());


    SumFitBuilder(List<String> fitPages) {
        this.fitPages = fitPages;
    }


    List<SumFitment> getFits(SumItem item) {
        List<SumFitment> result = new ArrayList<>();
        fitPages.forEach(page->{
            Document doc = Jsoup.parse(page);
            List<SumFitment> fits = buildFits(doc);
            result.addAll(fits);
        });
        result.forEach(fit->{
            fit.setItem(item);
        });

        return result;
    }

    private List<SumFitment> buildFits(Document doc) {
        List<SumFitment>  result = new ArrayList<>();
        Element resultEl = doc.getElementById("results");
        if (resultEl==null){
            return result;
        }
        Elements fitEls = resultEl.getElementsByClass("overview");
        fitEls.forEach(element -> {
            SumFitment fit = buildFit(element);
            result.add(fit);
        });

        return result;
    }

    private SumFitment buildFit(Element fitBlockElement) {
        SumFitment result = new SumFitment();
        List<SumFitAttribute> atts = new ArrayList<>();
        Elements propEls = fitBlockElement.getElementsByTag("p");
        propEls.forEach(element -> {
            Elements spanEls = element.getElementsByTag("span");
            String name = spanEls.get(0).text().trim();
            String value = spanEls.get(1).text().trim();
            SumFitAttribute att = new SumFitAttribute(name, value);
            att.setFitment(result);
            atts.add(att);
        });
        result.setAttributes(atts);

        return result;
    }
}
