/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import org.apache.commons.logging.LogFactory;
import seourl.Tools;

/**
 *
 * @author Yuri
 */
public class Link114Filter {

    protected HtmlPage page;
    protected WebClient webClient;
    private BrowserVersion browser;

    public Link114Filter() {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
        initWebClient();
    }

    private void initWebClient() {
        browser = new BrowserVersion.BrowserVersionBuilder(BrowserVersion.BEST_SUPPORTED).setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36").build();
        webClient = new WebClient(browser);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setTimeout(5000);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.waitForBackgroundJavaScript(5000);
        webClient.waitForBackgroundJavaScriptStartingBefore(5000);
        webClient.getCookieManager().setCookiesEnabled(false);
        webClient.getCurrentWindow().setInnerHeight(Integer.MAX_VALUE);
    }

    public boolean loadWeb() {
        long time;
        boolean status = false;

        time = System.currentTimeMillis();
        while (!status && ((System.currentTimeMillis() - time) < 10 * 1000)) {
            System.out.println("取得Link114頁面中....");
            try {
                page = webClient.getPage("http://www.link114.cn/");
                if (page.getWebResponse().getStatusCode() == 200) {
                    System.out.println("成功。");
                    status = true;
                }
                return true;
            } catch (Exception e) {
                System.out.println("失敗。");
                Tools.sleep(100);
            }
        }
        return false;
    }

    public boolean checkItem() {
        return true;

    }

    public boolean getRecord(String url) {
        return true;
    }

    public void test() {
        
        String urlParameters = "func=baidu_sl|baidu_qz_ai|baidu_qz_zz|so360_qz_zz|qqaq|wxaq|gfw&websites=google.com";
        byte[] postData = urlParameters.getBytes();
        int postDataLength = postData.length;
        String checkurl = "http://www.link114.cn/multi.php";

        try {
            URL connectto = new URL(checkurl);
            HttpURLConnection conn = (HttpURLConnection) connectto.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setInstanceFollowRedirects(false);
            conn.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + checkurl);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            System.out.println("WEB return value is : " + sb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
public void test2() {

        String checkurl = "http://www.link114.cn/get.php?func=gfw&site=moc.elgoog&r=47875151";

        try {
            URL connectto = new URL(checkurl);
            HttpURLConnection conn = (HttpURLConnection) connectto.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "text/html");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
            conn.setRequestProperty("Cookie", "latestversion=19.0811_1.038.030.970; preference=baidu_sl|baidu_qz_ai|baidu_qz_zz|so360_qz_zz|qqaq|wxaq|gfw");
            conn.setRequestProperty("charset", "utf-8");
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setInstanceFollowRedirects(false);
            conn.setDoOutput(true);


            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + checkurl);
            System.out.println("Response Code : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            System.out.println("WEB return value is : " + sb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
