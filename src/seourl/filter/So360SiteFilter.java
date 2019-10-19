/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter;

import com.gargoylesoftware.htmlunit.html.DomElement;
import java.util.Iterator;
import java.util.List;
import seourl.Configure;
import seourl.filter.ex.SearchEngineFilterAbstract;
import seourl.pack.So360SitePack;

/**
 *
 * @author yuri
 */
public class So360SiteFilter extends SearchEngineFilterAbstract {

    public So360SiteFilter(List<String> lkeyWords) {
        super("360SO-Site", lkeyWords);
    }

    public So360SitePack getSSP() {
        return (So360SitePack) this.getSep();
    }

    @Override
    protected String getPageUrl(String url, int i) {
        String sPage = "";
        if (i > 1) {
            sPage = "&pn=" + String.valueOf(i);
        }
        return String.format("https://www.so.com/s?q=site:%s%s", url, sPage);
    }

    @Override
    protected List<DomElement> getResultList() {
        return page.getByXPath("//ul[@class='result']/li[@class='res-list']");
    }

    @Override
    protected int getMaxPage() {
        int maxPage = 1;
        Iterator<DomElement> iterator = page.getElementById("page").getChildElements().iterator();
        DomElement dom;
        String tmp;
        while (iterator.hasNext() && maxPage < 3) {
            dom = iterator.next();
            tmp = dom.asText();
            if (tmp.contains("上一页") || tmp.contains("下一页") || tmp.contains("找到相关结果约")) {
                continue;
            }
            maxPage++;
        }
        return maxPage;
    }

    @Override
    final protected boolean doFilter(String tmp, String keyword, String url) {
        boolean pageIllegal = false;
        if (tmp.contains(keyword)) {
            this.getSSP().setIllegal(true);
            pageIllegal = true;
        }
        return pageIllegal;
    }

    @Override
    protected void createNewSearchEnginePack() {
        this.sep = new So360SitePack();
    }

    @Override
    protected boolean hasPageError() {
        return false;
    }

}
