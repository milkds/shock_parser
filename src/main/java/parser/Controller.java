package parser;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import parser.summit.SummitController;

import java.io.IOException;

public class Controller {
    public static void main(String[] args) throws InterruptedException, TesseractException, IOException {
        //new EibachController().processParsedPages();
        //new SumPartGetter().printPartsForKeystone();

     //   new SummitController().printParts();
   //     new SummitController().getParts("https://www.summitracing.com/search/brand/old-man-emu?PageSize=100&SortBy=Default&SortOrder=Default");
       new SummitController().getItemPages("ome");
    }


    private void tessTest(){
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("E://tesdata");
        tesseract.setLanguage("eng");
        tesseract.setPageSegMode(1);
        /*   tesseract.setOcrEngineMode(1);*/
     ///   System.out.println(tesseract.doOCR(new File("E://ocr.jpeg")));
    }


}
