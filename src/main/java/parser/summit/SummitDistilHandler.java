package parser.summit;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class SummitDistilHandler {
    private SumPagesGetter getter;
    private String url;
    private static final Logger logger = LogManager.getLogger(SummitDistilHandler.class.getName());

    public SummitDistilHandler(SumPagesGetter sumPagesGetter, String url) {
        this.getter = sumPagesGetter;
        this.url = url;
    }

    public String handleDistil(CloseableHttpResponse response, String pageCode) {
        printHeaders(response);
        String firstEntity = doFirstStep(pageCode);
        String secondEntity = doSecondStep(firstEntity);



        return "";
    }

    private String doSecondStep(String firstEntity) {
        String s;
        return "";
    }

    private String doFirstStep(String pageCode) {
        String distilJS = new SummitPageReader(pageCode).getFirstScriptName();
        logger.info("Script name "+distilJS);

        HttpGet request = new HttpGet("https://www.summitracing.com"+distilJS);
        request.addHeader("accept","*/*");
        request.addHeader("accept-encoding","gzip, deflate, br");
        request.addHeader("accept-language","ru-RU,ru;q=0.9");
        request.addHeader("sec-ch-ua",HeadersValueKeeper.SEC_CH_UA);
        request.addHeader("sec-ch-ua-mobile","?0");
        request.addHeader("sec-fetch-dest","script");
        request.addHeader("sec-fetch-mode","no-cors");
        request.addHeader("sec-fetch-site","same-origin");
        request.addHeader("user-agent",HeadersValueKeeper.USER_AGENT);

        getter.getContext().setAttribute(HttpClientContext.COOKIE_STORE, new BasicCookieStore());
        CloseableHttpResponse response = getResponse(request);
        String entity = getter.getPageFromResponse(response);
      //  System.out.println(entity);

        return entity;
    }

    private CloseableHttpResponse getResponse(HttpGet request) {
        CloseableHttpResponse response = null;
        try {
            response = getter.getHttpClient().execute(request, getter.getContext());
        } catch (IOException e) {
            logger.error("Couldn't execute request");
            e.printStackTrace();
        }
        return response;
    }

    private void printHeaders(CloseableHttpResponse response) {
        Header[] headers = response.getAllHeaders();
        for (Header header: headers){
            System.out.println(header.getName() + " : " + header.getValue());
        }
    }
}
