/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.event.Event;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import seourl.filter.ex.FilterAbstract;
import seourl.pack.*;

/**
 *
 * @author Yuri
 */
public class JumingFilter extends FilterAbstract {

    @Getter
    private JumingPack jp = new JumingPack();

    public JumingFilter() {
        super();
        this.loadCookie();
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
                //System.out.println(d.asXml());
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

    public boolean doAnalysis(String url) {
        this.jp = new JumingPack();
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
                            if (!(tmp2.indexOf("预定") >= 0 || tmp2.equals("立即注册"))) {
                                this.jp.setReg(true);
                            }
                            break;
                        case "QQ检测":
                            this.jp.setQq(!tmp2.equals("未拦截"));
                            break;
                        case "微信检测":
                            this.jp.setWeChat(!tmp2.equals("未拦截"));
                            break;
                        case "被墙/污染":
                            if (tmp2.equals("被墙") || tmp2.equals("污染 2  高")) {
                                this.jp.setGfw(true);
                            }
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
}
