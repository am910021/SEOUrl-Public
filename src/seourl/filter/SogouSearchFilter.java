/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter;

import com.gargoylesoftware.htmlunit.html.DomElement;
import java.util.List;
import seourl.Configure;
import seourl.filter.ex.SearchEngineFilterAbstract;
import seourl.pack.SogouSerachPack;

/**
 *
 * @author yuri
 */
public class SogouSearchFilter extends SearchEngineFilterAbstract {

    public SogouSearchFilter(List<String> lkeyWords) {
        super("Sogou-Search", lkeyWords);
    }

    public SogouSerachPack getSSP() {
        return (SogouSerachPack) this.getSep();
    }

    @Override
    final protected String getPageUrl(String url, int i) {
        String sPage = "";
        if (i > 1) {
            sPage = "&page=" + String.valueOf(i);
        }
        return String.format(Configure.SOGOU_SEARCH + "%s%s", url, sPage);
    }

    @Override
    final protected List<DomElement> getResultList() {
        return page.getByXPath("//div[@class='results']/div[@class='vrwrap']");
    }

    @Override
    protected int getMaxPage() {
        int maxPage = 1;
        DomElement p = page.getElementById("pagebar_container");
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
        if ((tmp.contains(keyword)) && (tmp.contains(url))) {
            this.getSSP().setIllegal(true);
            pageIllegal = true;
        }
        return pageIllegal;
    }

    @Override
    protected void createNewSearchEnginePack(String url) {
        this.sep = new SogouSerachPack(url);
    }

    @Override
    protected boolean hasPageError() {
//        if(page.asText().contains("异常访问")){
//            System.out.println(page.getUrl());
//            System.out.println(page.asXml());
//        }

        return page.asText().contains("异常访问");
    }
}
