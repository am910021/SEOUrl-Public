/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter;

import com.gargoylesoftware.htmlunit.html.DomElement;
import java.util.List;
import seourl.other.Configure;
import seourl.filter.ex.DomainFilterAbstract;
import seourl.pack.BaiduDomainPack;
import seourl.type.Filter;

/**
 *
 * @author yuri
 */
public class BaiduDomainFilter extends DomainFilterAbstract {

    public BaiduDomainFilter(int pid, Filter filter, List<String> keywords) {
        super(pid, filter, keywords);
    }

    public BaiduDomainPack getBDP() {
        return (BaiduDomainPack) this.getSep();
    }

    @Override
    final protected String getPageUrl(String url, int i) {
        String sPage = "";
        if (i > 1) {
            sPage = "&pn=" + String.valueOf(i * 10);
        }
        return String.format(Configure.BAIDU_DOMAIN + "%s%s", url, sPage);
    }

    @Override
    protected List<DomElement> getResultList() {
        return page.getByXPath("//div[@class='result c-container ']");
    }

    @Override
    protected int getMaxPage() {
        int maxPage = 1;
        DomElement p = page.getElementById("page");

        if (p != null) {
            maxPage = p.asText().split("\n").length;
            if (maxPage >= 3) {
                maxPage = 3;
            }
        }
        return maxPage;
    }

    @Override
    protected void createNewSearchEnginePack(String url) {
        this.sep = new BaiduDomainPack(url);
    }

    @Override
    protected boolean hasPageError() {
        return false;
    }

}
