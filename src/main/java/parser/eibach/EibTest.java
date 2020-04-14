package parser.eibach;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EibTest {

   public void testItemBuild(){
        String contents = "";
        try {
             contents = new String(Files.readAllBytes(Paths.get("src\\main\\resources\\test.html")));
        } catch (IOException e) {
            e.printStackTrace();
        }
       Document doc = Jsoup.parse(contents);
        new ItemBuilder(doc, "https://eibach.com/us/4-0138-1982-1992-pontiac-firebird-sportline-kit").buildItem();

    }
}
