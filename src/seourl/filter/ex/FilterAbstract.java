/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter.ex;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ConfirmHandler;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
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
import java.util.Set;
import java.util.logging.Level;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.LogFactory;
import seourl.type.Device;
import seourl.Tools;

/**
 *
 * @author yuri
 */
public abstract class FilterAbstract extends BasicFilterAbstract {

    protected HtmlPage page;
    protected WebClient webClient;
    protected BrowserVersion browser;

    public abstract boolean doAnalysis(String url);
    protected abstract void createNewSearchEnginePack(String url);
    
    @Getter
    @Setter
    private String cookie = "cookie.bin";
    @Getter
    private String cookiePath = "cache/";

    protected FilterAbstract(String filterType) {
        super(filterType);
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
        initWebClient();
    }

    public void setCookiePath(String path) {
        Tools.checkDir(path);
        this.cookiePath = path;
    }

    protected void initWebClient() {
        ConfirmHandler okHandler = new ConfirmHandler() {
            public boolean handleConfirm(Page page, String message) {
                return true;
            }
        };
        int r = Tools.getRandomNumberInRange(0, 7);

        browser = new BrowserVersion.BrowserVersionBuilder(BrowserVersion.BEST_SUPPORTED).setUserAgent(Device.getById(r).getType()).build();
        webClient = new WebClient(browser);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setTimeout(5000);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.waitForBackgroundJavaScriptStartingBefore(3000);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getCurrentWindow().setInnerHeight(Integer.MAX_VALUE);
        webClient.setConfirmHandler(okHandler);
    }

    public final boolean loadWeb(String url) {
        long time;
        boolean status = false;

        time = System.currentTimeMillis();
        while (!status && ((System.currentTimeMillis() - time) < 10 * 1000)) {
            try {
                page = webClient.getPage(url);
                if (page.getWebResponse().getStatusCode() == 200) {
                    System.out.printf("取得%s頁面中....成功。\n", url);
                    status = true;
                }
                return true;
            } catch (Exception e) {
                Tools.printError(filterType, e);
                System.out.printf("取得%s頁面中....失敗。\n", url);
                Tools.sleep(100);
            }
        }
        Tools.sleep(1, 1000);
        return false;
    }

    public final void close() {
        webClient.close();
        webClient = null;
        page = null;
    }

    public final void loadCookie() {
        ObjectInputStream in = null;
        try {
            File file = new File(this.cookiePath + this.cookie);
            if (!file.exists()) {
                return;
            }
            in = new ObjectInputStream(new FileInputStream(file));
            @SuppressWarnings("unchecked")
            Set<Cookie> cookies = (Set<Cookie>) in.readObject();
            Iterator<Cookie> i = cookies.iterator();
            while (i.hasNext()) {
                webClient.getCookieManager().addCookie(i.next());
            }
            in.close();
        } catch (Exception ex) {
            Tools.printError(filterType, ex);
        }
        webClient.getCookieManager().clearExpired(new Date());
    }

    public final void saveCookie() {
        try {
            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(this.cookiePath + this.cookie));
            out.writeObject(webClient.getCookieManager().getCookies());
            out.close();
        } catch (IOException ex) {
            Tools.printError(filterType, ex);
        }
    }
    
    protected final void cleanMemory(){
        if(page != null){
            page.cleanUp();
        }
    }
    
    protected final void cleanMenory(HtmlPage p){
        if(p!=null){
            p.cleanUp();
        }
    }
}
