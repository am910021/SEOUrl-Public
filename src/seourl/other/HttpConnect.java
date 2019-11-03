/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.other;

import java.io.InputStream;
import lombok.Getter;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import seourl.type.Device;

/**
 *
 * @author yuri
 */
public class HttpConnect {

    @Getter
    private String baseURL;
    @Getter
    private int status = 0;

    private int timeout;
    private boolean utf8;

    public HttpConnect(String url, int timeout, boolean utf8) {
        this.baseURL = url;
        this.timeout = timeout;
    }

    public HttpConnect(String url, int timeout) {
        this.baseURL = url;
        this.timeout = timeout;
    }

    public String getString() {
        return getDocument().text();
    }

    public Document getDocument() {

        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder.setConnectTimeout(this.timeout);
        requestBuilder.setConnectionRequestTimeout(this.timeout);
        requestBuilder.setSocketTimeout(30 * 1000);
        requestBuilder.setRedirectsEnabled(true);
        if (Configure.localAddress != null) {
            requestBuilder.setLocalAddress(Configure.localAddress);
        }

        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultRequestConfig(requestBuilder.build());
        builder.setUserAgent(Device.getById(Tools.getRandomNumberInRange(0, 7)).getType());

        HttpGet httpGet = new HttpGet(this.baseURL);
        if (utf8) {
            httpGet.setHeader("Accept-Charset", "utf-8");
            httpGet.setHeader("contentType", "utf-8");
        }
        Document doc = null;
        try (CloseableHttpClient client = builder.build(); CloseableHttpResponse response = client.execute(httpGet); InputStream in = response.getEntity().getContent()) {
            doc = Jsoup.parse​(in, null, httpGet.getURI().toASCIIString());
            baseURL = httpGet.getURI().toASCIIString();
            status = response.getStatusLine().getStatusCode();
            EntityUtils.consume(response.getEntity());
        } catch (Exception e) {
            Tools.printError(HttpConnect.class.getName(), e);
        } finally {
            httpGet.releaseConnection();
        }
        return doc;
    }
}
