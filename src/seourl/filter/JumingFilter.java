/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ConfirmHandler;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.event.Event;
import com.gargoylesoftware.htmlunit.util.Cookie;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.LogFactory;
import seourl.Tools;
import seourl.pack.*;

/**
 *
 * @author Yuri
 */
public class JumingFilter{

    protected HtmlPage page;
    protected WebClient webClient;
    private BrowserVersion browser;
    
    JumingPack jp = new JumingPack();

    public JumingFilter() {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
        initWebClient();
    }

    private void initWebClient() {
        ConfirmHandler okHandler = new ConfirmHandler() {
            public boolean handleConfirm(Page page, String message) {
                return true;
            }
        };

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
        webClient.waitForBackgroundJavaScript(3000);

        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getCurrentWindow().setInnerHeight(Integer.MAX_VALUE);
        webClient.setConfirmHandler(okHandler);
        this.loadCookie();
    }

    public boolean loadWeb() {
        long time;
        boolean status = false;

        time = System.currentTimeMillis();
        while (!status && ((System.currentTimeMillis() - time) < 10 * 1000)) {
            try {
                page = webClient.getPage("http://www.juming.com");
                if (page.getWebResponse().getStatusCode() == 200) {
                    System.out.println("取得juming頁面中....成功。");
                    status = true;
                }
                return true;
            } catch (Exception e) {
                System.out.println("取得juming頁面中....失敗。");
                Tools.sleep(100);
            }
        }
        Tools.sleep(1, 1000);
        return false;
    }

    public boolean login() {
        if (page.getElementById("jm-topbar").asXml().indexOf("登录后") >= 0) {
            System.out.println("取得歷史登入資料....成功。");
            return true;
        }
        boolean login = false;
        HtmlForm form = page.getHtmlElementById("loginBox");
        form.getInputByName("re_yx").setValueAttribute("account");
        form.getInputByName("re_mm").setValueAttribute("password");
        try {
            form.fireEvent(Event.TYPE_SUBMIT);
            Page p = page.refresh();
            if (p.isHtmlPage()) {
                page = ((HtmlPage) p);

                DomElement d = page.getElementById("jm-topbar");
                System.out.println(d.asXml());
                if (d.asXml().indexOf("登录后") >= 0) {
                    login = true;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(JumingFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (login) {
            System.out.println("登入juming中....成功。");
        } else {
            System.out.println("登入juming中....失敗。");
        }
        saveCookie();
        return login;
    }

    public void close() {
        webClient.close();
    }

    private void loadCookie() {
        ObjectInputStream in = null;
        try {
            File file = new File("cookie.file");
            if (!file.exists()) {
                return;
            }
            in = new ObjectInputStream(new FileInputStream(file));
            Set<Cookie> cookies = (Set<Cookie>) in.readObject();
            Iterator<Cookie> i = cookies.iterator();
            while (i.hasNext()) {
                webClient.getCookieManager().addCookie(i.next());
            }
            in.close();
        } catch (Exception ex) {
            Logger.getLogger(JumingFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        webClient.getCookieManager().clearExpired(new Date());
    }

    private void saveCookie() {
        try {
            ObjectOutput out = new ObjectOutputStream(new FileOutputStream("cookie.file"));
            out.writeObject(webClient.getCookieManager().getCookies());
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(JumingFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean doAnalysis(String url) {
        try {
            page = webClient.getPage(String.format("http://www.juming.com/hao/?cha_ym=%s", url));
            List<DomElement> table = page.getByXPath("//div[@class='orders']/div/table/tbody/tr");
            String tmp;
            String tmp2;
            for (int i = 0; i < table.size() - 1; i += 2) {
                Iterator<DomElement> ths = table.get(i).getChildElements().iterator();
                Iterator<DomElement> tds = table.get(i + 1).getChildElements().iterator();
                while (ths.hasNext() && tds.hasNext()) {
                    tmp = ths.next().asText().replace(" ", "");
                    tmp2 = tds.next().asText().replace(" ", "");
                    switch (tmp) {
                        case "注册状态":
                            if(tmp2.indexOf("预定") < 0 && !tmp2.equals("立即注册")){
                                this.jp.setReg(false);
                            }
                            break;
                        case "QQ检测":
                            this.jp.setQq(!tmp2.equals("未拦截"));
                            break;
                        case "微信检测":
                            this.jp.setWeChat(!tmp2.equals("未拦截"));
                            break;
                        case "被墙/污染":
                            this.jp.setGfw(!tmp2.equals("正常"));
                            break;
                    }
                }
            }
            System.out.printf("取得juming中的 %s 訊息....成功。\n", url);
            return true;
        } catch (Exception ex) {
            //Logger.getLogger(JumingFilter.class.getName()).log(Level.SEVERE, null, ex);
           System.out.printf("取得juming中的 %s 訊息....失敗。\n", url);
           this.jp.setError(true);
        }
        return false;
    }
    
    public JumingPack getJP(){
        JumingPack tmp = this.jp;
        this.jp = new JumingPack();
        return tmp;
                
    }

}
