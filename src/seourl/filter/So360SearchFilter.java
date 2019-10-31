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
import seourl.pack.So360SerachPack;

/**
 *
 * @author yuri
 */
public class So360SearchFilter extends SearchEngineFilterAbstract {

    public So360SearchFilter(List<String> lkeyWords) {
        super("360SO-Search", lkeyWords);
    }

    public So360SerachPack getSSP() {
        return (So360SerachPack) this.getSep();
    }

    @Override
    protected String getPageUrl(String url, int i) {
        String sPage = "";
        if (i > 1) {
            sPage = "&pn=" + String.valueOf(i);
        }
        return String.format(Configure.SO360_SEARCH + "%s%s", url, sPage);
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
        if ((tmp.contains(keyword)) && (tmp.contains(url))) {
            this.getSSP().setIllegal(true);
            pageIllegal = true;
        }
        return pageIllegal;
    }

    @Override
    protected void createNewSearchEnginePack(String url) {
        this.sep = new So360SerachPack(url);
    }

    @Override
    protected boolean hasPageError() {
        return false;
    }

}
