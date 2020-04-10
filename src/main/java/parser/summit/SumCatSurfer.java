package parser.summit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import parser.utils.SileniumUtil;

import java.util.List;

public class SumCatSurfer {
    private static final Logger logger = LogManager.getLogger(SumCatSurfer.class.getName());

    public void printPartsFromCategory(List<String> catUrl){
        WebDriver driver = initDriver(catUrl.get(0));
        printResultsForCat(driver);
        for (int i = 1; i < catUrl.size() ; i++) {
            driver.get(catUrl.get(i));
            waitForResults(driver, catUrl.get(i));
            printResultsForCat(driver);
        }
        driver.quit();
    }

    private void printResultsForCat(WebDriver driver) {
        while (true){
            printResults(driver);
            if (hasNextPage(driver)){
                openNextPage(driver);
            }
            else {
                break;
            }
        }

    }

    private void openNextPage(WebDriver driver) {
        WebElement nextPageEl = driver.findElement(By.cssSelector("div[class='next-page']"));
        String nextPageUrl = nextPageEl.findElement(By.tagName("a")).getAttribute("href");
        driver.get(nextPageUrl);
        waitForResults(driver, nextPageUrl);
    }

    private void waitForResults(WebDriver driver, String catUrl) {
        try {
            By by = By.id("results");
              SileniumUtil.waitForElementPresent(driver, by, 60);
        }
        catch (TimeoutException e){
            logger.error("Couldn't load start page " + catUrl);
            driver.quit();
            System.exit(1);
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean hasNextPage(WebDriver driver) {
        try {
            driver.findElement(By.cssSelector("div[class='next-page']"));
        }
        catch (NoSuchElementException e){
            return false;
        }
        return true;
    }

    private void printResults(WebDriver driver) {
        WebElement resultsEl = driver.findElement(By.id("results"));
        List<WebElement> resElems = resultsEl.findElements(By.cssSelector("div[class='item clearfix']"));
        resElems.forEach(resEl->{
            WebElement curEl = resEl.findElement(By.className("title"));
            curEl = curEl.findElement(By.className("suggestBeatAPrice"));
            String part = curEl.getAttribute("data-prodid");
            part = part.replace("MON-", "");
            System.out.println("https://wwwsc.ekeystone.com/Search/Detail?pid=MON" + part);
        });
    }

    private WebDriver initDriver(String catUrl) {
      //  System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
        System.setProperty("webdriver.edge.driver", "src\\main\\resources\\msedgedriver.exe");


        ChromeOptions options = new ChromeOptions()/*.addArguments("--proxy-server=http://" + "54.154.10.149:3128")*/;

        options.setBinary("C:\\Windows\\SystemApps\\Microsoft.MicrosoftEdge_8wekyb3d8bbwe\\MicrosoftEdge.exe");
        EdgeOptions eOpt = new EdgeOptions().merge(options);
        options.addArguments("--disable-extensions");
        options.addArguments("--profile-directory=Default");
        options.addArguments("--disable-plugins-discovery");
        options.addArguments("--start-maximized");
        options.addArguments("--enable-javascript");
        options.addArguments("--user-agent='Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36'");
     /*   ChromeOptions options = new ChromeOptions();


        WebDriver driver = new ChromeDriver(options);*/
        WebDriver driver = new EdgeDriver(eOpt);
        //driver.manage().deleteAllCookies();

        driver.get(catUrl);
       waitForResults(driver, catUrl);

       return driver;
    }


    /*public void printPartsFromCategory(String catUrl){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpClientContext context = HttpClientContext.create();
        HttpGet httpGet = new HttpGet(catUrl);
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");
        CloseableHttpResponse response1 = null;
        try {
             response1 = httpclient.execute(httpGet, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            System.out.println(response1.getStatusLine());
            Header[] headers = response1.getAllHeaders();
            for (Header header: headers) {
                System.out.println("Key [ " + header.getName() + "], Value[ " + header.getValue() + " ]");
            }
                      HttpEntity entity1 = response1.getEntity();
            String responseString = EntityUtils.toString(entity1, "UTF-8");
            System.out.println(responseString);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


     *//*   try {
            Document doc = Jsoup.connect(catUrl).get();
            Elements testEls = doc.getElementsByClass("item clearfix");
            testEls.forEach(testEl->{
                System.out.println(testEl.text());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }*//*
    }*/
}
