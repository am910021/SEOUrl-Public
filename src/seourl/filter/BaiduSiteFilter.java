/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter;

import com.gargoylesoftware.htmlunit.html.DomElement;
import java.util.List;
import seourl.other.Configure;
import seourl.filter.ex.SearchEngineFilterAbstract;
import seourl.pack.BaiduSitePack;

/**
 *
 * @author yuri
 */
public class BaiduSiteFilter extends SearchEngineFilterAbstract {

    public BaiduSiteFilter(int pid,List<String> lkeyWords) {
        super(pid, "Baidu-Site", lkeyWords);
    }

    public BaiduSitePack getBSP() {
        return (BaiduSitePack) this.getSep();
    }

    @Override
    protected String getPageUrl(String url, int i) {
        String sPage = "";
        if (i > 1) {
            sPage = "&pn=" + String.valueOf(i);
        }
        return String.format(Configure.BAIDU_SITE + "%s%s", url, sPage);
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
    final protected boolean doFilter(String tmp, String keyword, String url) {
        boolean pageIllegal = false;
        if (tmp.contains(keyword)) {
            this.getBSP().setIllegal(true);
            pageIllegal = true;
        }
        return pageIllegal;
    }

    @Override
    protected void createNewSearchEnginePack(String url) {
        this.sep = new BaiduSitePack(url);
    }

    @Override
    protected boolean hasPageError() {
        return false;
    }

}
