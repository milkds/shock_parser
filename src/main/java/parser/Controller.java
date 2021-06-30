package parser;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;
import parser.summit.SummitController;
import parser.utils.BasicUtils;
import parser.utils.TestUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Controller {
    public static void main(String[] args) throws InterruptedException, TesseractException, IOException {
     //   new Controller().processInput();
        //new EibachController().processParsedPages();
        //new SumPartGetter().printPartsForKeystone();

     //   new SummitController().printParts();
        new SummitController().getParts("https://www.summitracing.com/int/search/brand/koni?SortBy=Default&SortOrder=Ascending&GroupBy=SKU&PageSize=100");
  //     new SummitController().getItemPages("moog");
     // new SummitController().checkParseConsistency("ome");
   //    new SummitController().saveItemsToDB("moog");

       // new TestUtil().testProxyHttpClient();
    }


    private void tessTest(){
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("E://tesdata");
        tesseract.setLanguage("eng");
        tesseract.setPageSegMode(1);
        /*   tesseract.setOcrEngineMode(1);*/
     ///   System.out.println(tesseract.doOCR(new File("E://ocr.jpeg")));
    }

    private void processInput(){
        List<String> input = BasicUtils.getInputInfo();
        Set<String> notes = new HashSet<>();
        for (String s: input){
            if (s.contains("build item")){
                continue;
            }
            notes.add(StringUtils.substringBetween(s, "Unknown Application Note ", " at item"));
        }
        notes.forEach(System.out::println);
    }


}
