package parser.summit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpReqUtil {



    public String getHTMLfromURL(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";

        try {

            HttpGet request = new HttpGet(url);

            // add request headers
           /* request.addHeader(":authority", "www.summitracing.com");
            request.addHeader(":method", "GET");
            request.addHeader(":path", "/");
            request.addHeader(":scheme", "https");*/
            request.addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            request.addHeader("accept-encoding", "gzip, deflate, br");
            request.addHeader("accept-language", "ru-RU,ru;q=0.9");
            request.addHeader("sec-ch-ua", "ru-RU,ru;q=0.9");
            request.addHeader("sec-ch-ua-mobile", "?0");
            request.addHeader("sec-fetch-dest", "document");
            request.addHeader("sec-fetch-mode", "navigate");
            request.addHeader("sec-fetch-site", "none");
            request.addHeader("sec-fetch-user", "?1");
            request.addHeader("upgrade-insecure-requests", "1");
            request.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36");
            CloseableHttpResponse response = httpClient.execute(request);

            try {

                // Get HttpResponse Status
               /* System.out.println(response.getProtocolVersion());              // HTTP/1.1
                System.out.println(response.getStatusLine().getStatusCode());   // 200
                System.out.println(response.getStatusLine().getReasonPhrase()); // OK
                System.out.println(response.getStatusLine().toString());        // HTTP/1.1 200 OK*/

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    result = EntityUtils.toString(entity);
                    /*System.out.println(result);*/
                }
                else {
                }
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }

        return result;
    }
}
