/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.other;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.Getter;
import lombok.Setter;
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

    @Setter
    private boolean isJson = false;

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

    public String getJson() {
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
        if (isJson) {
            httpGet.setHeader("Accept-Charset", "utf-8");
            httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");
        }
        String json = "";
        try (CloseableHttpClient client = builder.build(); CloseableHttpResponse response = client.execute(httpGet); InputStream in = response.getEntity().getContent()) {
            baseURL = httpGet.getURI().toASCIIString();
            status = response.getStatusLine().getStatusCode();
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    json = sb.toString();
            }
            EntityUtils.consume(response.getEntity());
        } catch (Exception e) {
            Tools.printError(HttpConnect.class.getName(), e);
        } finally {
            httpGet.releaseConnection();
        }
        return json;
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
        if (isJson) {
            httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");
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
