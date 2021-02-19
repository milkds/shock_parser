package parser.summit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SumPartGetter {

    public void printPartsForKeystone(){
        File[] files = new File("d:/shocks/king").listFiles();
        for (File file: files){
            try {
                Document document = Jsoup.parse(file, "UTF-8");
                printPartsFromDoc(document);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void printPartsFromDoc(Document doc) {
        Element resEl = doc.getElementById("sResultsComp");
        Elements partElements = resEl.getElementsByAttribute("data-prodid");
        partElements.forEach(partEl->{
       //     Element finalEl = partEl.getElementsByClass("suggestBeatAPrice").first();
           try {
               String url = "https://wwwsc.ekeystone.com/Search/Detail?pid="+partEl.attr("data-prodid");
               url = url.replace("KSS-", "KSH");
               System.out.println(url);
               //System.out.println("https://wwwsc.ekeystone.com/Search/Detail?pid="+partEl.attr("data-prodid"));
           }
           catch (NullPointerException e){
           }
        });

    }

}
