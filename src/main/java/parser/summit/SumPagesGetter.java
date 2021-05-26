package parser.summit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class SumPagesGetter {
    private static final Logger logger = LogManager.getLogger(SumPagesGetter.class.getName());


    private CloseableHttpClient httpClient;
    private HttpClientContext context;
    private String referer = "";


    public String getValidPage(String url){
        String result = "";
        HttpGet request = getBasicGetRequest(url);
        prepareContext();
        CloseableHttpResponse response = getResponse(request);
        result = processResponse(response, url);
        logger.info("Got page " + url);

        return result;
    }

    private void setCookies(HttpGet request) {
        if (context==null){
            return;
        }
        List<Cookie> cookies = context.getCookieStore().getCookies();
        if (cookies.size()>0){
            for (Cookie cookie: cookies){
                request.addHeader("cookie",cookie.getName()+"="+cookie.getValue());
            }
        }
    }

    String getPageFromResponse(CloseableHttpResponse response) {
        String result = "";
        HttpEntity entity = response.getEntity();
        try {
            result = EntityUtils.toString(entity);
        } catch (IOException e) {
            logger.error("couldn't get entity from response");
        }

       // System.out.println(result);
        return result;
    }

    private void prepareContext() {
        if (context!=null){
           /* logger.info("Cookies: ");
            for (Cookie cookie: context.getCookieStore().getCookies()){
                System.out.println(cookie.getName() + " : " + cookie.getValue());
            }*/
            return;
        }
        context = HttpClientContext.create();
        context.setAttribute(HttpClientContext.COOKIE_STORE, new BasicCookieStore());
    }

    private String processResponse(CloseableHttpResponse response, String url) {
        if (response==null){
            return "";
        }
        String pageCode = getPageFromResponse(response);
        if (!SummitPageReader.isDistil(pageCode)){
            avoidDistil(response, url, pageCode);
            closeResponse(response);
            return pageCode;
        }
        logger.info("Got thrown to distil");
        pageCode = new SummitDistilHandler(this, url).handleDistil(response, pageCode);

        return pageCode;

    }

    private void avoidDistil(CloseableHttpResponse response, String url, String pageCode) {
        logger.info("Sending avoid distill script");
        sendFirstRequest(response, url, pageCode);
        /*logger.info("Cookies: ");
        for (Cookie cookie: context.getCookieStore().getCookies()){
            System.out.println(cookie.getName() + " : " + cookie.getValue());
        }*/
       /*
        logger.info("Response Headers");
        Header[] headers = response.getAllHeaders();
        for (Header head: headers){
            System.out.println(head.getName() + " : " + head.getValue());
        }*/

    }

    private void sendFirstRequest(CloseableHttpResponse response, String url, String pageCode) {
        String scriptUrl = new SummitPageReader(pageCode).getHeadScriptName();
        if (scriptUrl.length()==0){
            return;
        }
        HttpGet request = new HttpGet("https://www.summitracing.com"+scriptUrl);
        request.addHeader("accept","*/*");
        request.addHeader("accept-encoding","gzip, deflate, br");
        request.addHeader("accept-language","ru-RU,ru;q=0.9");
        request.addHeader("referer",url);
        request.addHeader("sec-ch-ua",HeadersValueKeeper.SEC_CH_UA);
        request.addHeader("sec-ch-ua-mobile","?0");
        request.addHeader("sec-fetch-dest","script");
        request.addHeader("sec-fetch-mode","no-cors");
        request.addHeader("sec-fetch-site","same-origin");
        request.addHeader("user-agent",HeadersValueKeeper.USER_AGENT);
        try {
            CloseableHttpResponse resp = httpClient.execute(request, context);
            resp.close();
        } catch (IOException e) {
            logger.error("couldn't execute request");
        }

    }

    private void closeResponse(CloseableHttpResponse response) {
        try {
            response.close();
        }
        catch (IOException e){
            logger.error("Couldn't close response");
        }
    }

    private CloseableHttpResponse getResponse(HttpGet request) {
        CloseableHttpResponse result = null;
        try {
            result = httpClient.execute(request, context);
        } catch (IOException e) {
            logger.error("Couldn't execute request");
            e.printStackTrace();
        }
        /*CookieStore cookieStore = context.getCookieStore();
        System.out.println("Cookies");
        for (Cookie cookie: cookieStore.getCookies()){
            System.out.println(cookie.getName() + " : " + cookie.getValue());
        }*/

        return result;
    }

    private HttpGet getBasicGetRequest(String url) {
        HttpGet result = new HttpGet(url);
        addBasicHeaders(result);
        if (referer.length()!=0){
           result.addHeader("referer", referer);
        }
        referer = url;

        return result;
    }

    private void addBasicHeaders(HttpGet request) {
        request.addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        request.addHeader("accept-encoding", "gzip, deflate, br");
        request.addHeader("accept-language", "ru-RU,ru;q=0.9");
        request.addHeader("sec-ch-ua", "Chromium\";v=\"88\", \"Google Chrome\";v=\"88\", \";Not A Brand\";v=\"99");
        request.addHeader("sec-ch-ua-mobile", "?0");
        request.addHeader("sec-fetch-dest", "document");
        request.addHeader("sec-fetch-mode", "navigate");
        request.addHeader("sec-fetch-site", "none");
        request.addHeader("sec-fetch-user", "?1");
        request.addHeader("upgrade-insecure-requests", "1");
        request.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36");
    }


    public SumPagesGetter() {
        /*HttpHost proxy = new HttpHost("103.214.46.60", 12334, "https");
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
         httpClient = HttpClients.custom()
                .setRoutePlanner(routePlanner)
                .build();
*/
      httpClient = HttpClients.createDefault();
      context = null;
    }

    public HttpClientContext getContext() {
        return context;
    }

    public void setContext(HttpClientContext context) {
        this.context = context;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    String getValidPageNoRef(String url) {
        referer = "";

        return getValidPage(url);
    }
}
