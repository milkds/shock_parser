package parser.utils;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class TestUtil {

    public void testProxyHttpClient(){
        String url = "https://www.summitracing.com/";
    //    HttpHost proxy = new HttpHost("161.202.226.194", 80);
        HttpHost proxy = new HttpHost("51.38.82.244", 443);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        HttpClient httpclient = HttpClients.custom()
                .setRoutePlanner(routePlanner)
                .build();

        String result = getResp(httpclient, url);
        System.out.println(result);
        System.exit(0);
    }

    private String getResp(HttpClient httpclient, String url) {
        String result = "";

        try {

            HttpGet request = new HttpGet(url);

            // add request headers
           /* request.addHeader(":authority", "www.summitracing.com");
            request.addHeader(":method", "GET");
            request.addHeader(":path", "/");
            request.addHeader(":scheme", "https");*/
//            request.addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
//            request.addHeader("accept-encoding", "gzip, deflate, br");
//            request.addHeader("accept-language", "ru-RU,ru;q=0.9");
//            request.addHeader("sec-ch-ua", "ru-RU,ru;q=0.9");
//            request.addHeader("sec-ch-ua-mobile", "?0");
//            request.addHeader("sec-fetch-dest", "document");
//            request.addHeader("sec-fetch-mode", "navigate");
//            request.addHeader("sec-fetch-site", "none");
//            request.addHeader("sec-fetch-user", "?1");
//            request.addHeader("upgrade-insecure-requests", "1");
//            request.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36");
            HttpResponse response = httpclient.execute(request);

            try {

                // Get HttpResponse Status
               /* System.out.println(response.getProtocolVersion());              // HTTP/1.1
                System.out.println(response.getStatusLine().getStatusCode());   // 200
                System.out.println(response.getStatusLine().getReasonPhrase()); // OK
                System.out.println(response.getStatusLine().toString());        // HTTP/1.1 200 OK*/

                HttpEntity entity = response.getEntity();
                System.out.println(response.getStatusLine());
                for (Header header: response.getAllHeaders()){
                    System.out.println(header);
                }
                if (entity != null) {
                    // return it as a String
                    result = EntityUtils.toString(entity);
                    /*System.out.println(result);*/
                }
                else {
                    System.out.println("Null entity");
                }
            } finally {

            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

        return result;
    }
}
