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
        File[] files = new File("d:/shocks/monroe").listFiles();
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
        Element resEl = doc.getElementById("results");
        Elements partElements = resEl.getElementsByClass("title");
        partElements.forEach(partEl->{
            Element finalEl = partEl.getElementsByClass("suggestBeatAPrice").first();
           try {
               System.out.println("https://wwwsc.ekeystone.com/Search/Detail?pid="+finalEl.attr("data-prodid"));
           }
           catch (NullPointerException e){
           }
        });

    }

}
