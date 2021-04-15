package parser.summit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import parser.utils.BasicUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SumPageToDiscUtil {
    private String brand;
    private static final String PATH = "c:/pages/";
    private static final Logger logger = LogManager.getLogger(SumPageToDiscUtil.class.getName());

    SumPageToDiscUtil(String brand) {
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

    Map<String, List<String>> getFileNamesForBrand() {
        List<String> fNames = new ArrayList<>();
        File[] files = new File(PATH+brand).listFiles();

        for (File file : files) {
            fNames.add(file.getName());
        }

        return groupFileNamesByPart(fNames);
    }

    private Map<String, List<String>> groupFileNamesByPart(List<String> fNames) {
        Map<String, List<String>> result = new HashMap<>();
        fNames.forEach(fName->{
            if (!fName.contains("fit")){
                result.put(fName, new ArrayList<>());
            }
            else {
                String key = StringUtils.substringBefore( fName,"_fit")+".txt";
                result.get(key).add(fName);
            }
        });

        return result;
    }

    String getPageText(String pageName) {
        String result = "";
        String fileName = PATH+brand+"/"+pageName;
        Path fPath = Path.of(fileName);
        try {
            result = Files.readString(fPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     *
     * @param v - List of file names to read from disk.
     * @return Map where key - file name and v - this file's content.
     */
    Map<String, String> getNameContentMap(List<String> v) {
        Map<String, String> result = new HashMap<>();
        v.forEach(fName->{
            String content = getPageText(fName);
            result.put(fName, content);
        });

        return result;
    }

    Map<String, List<String>> getAllPagesForBrand() {
        logger.info("reading pages from disc");
        Map<String, List<String>> result = new HashMap<>();
        Map<String, List<String>> fNamesMap = getFileNamesForBrand();
        fNamesMap.forEach((k,v)->{
            String mainPageCont = getPageText(k);
            List<String> fits = new ArrayList<>();
            v.forEach(fitName-> fits.add(getPageText(fitName)));
            result.put(mainPageCont, fits);
        });
        logger.info("pages read");

        return result;
    }
}
