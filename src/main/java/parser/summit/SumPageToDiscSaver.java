package parser.summit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import parser.utils.BasicUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class SumPageToDiscSaver {
    private String brand;
    private static final String PATH = "c:/pages/";
    private static final Logger logger = LogManager.getLogger(SumPageToDiscSaver.class.getName());

    SumPageToDiscSaver(String brand) {
        this.brand = brand;
        checkDir(brand);
    }

    private void checkDir(String brand) {
        String fName = PATH+brand;
        try {
            Files.createDirectories(Paths.get(fName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePage(String curUrl, String currentPage){
       String fName = getFileName(curUrl);
       fName = PATH+brand+"/"+fName;
       logger.info("File name - " + fName);
        try {
            BasicUtils.saveTextToFile(currentPage, fName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileName(String curUrl) {
        String result = getPartFromUrl(curUrl);
        if (isFit(curUrl)){
            result = result+"_fit";
            int fitNumber = getFitNumber(curUrl);
            result = result+fitNumber;
        }
        result = result + ".txt";
        return result;
    }

    private int getFitNumber(String curUrl) {
        if (!curUrl.contains("page")){
            return 1;
        }
        String pageNo = StringUtils.substringAfter( curUrl,"page=");

        return Integer.parseInt(pageNo);
    }

    private boolean isFit(String curUrl) {
        return curUrl.contains("applications");
    }

    private String getPartFromUrl(String curUrl) {
        String result = StringUtils.substringAfter(curUrl, "parts/");
        if (result.contains("/")){
            result = StringUtils.substringBefore(result, "/");
        }

        return result;
    }
}
