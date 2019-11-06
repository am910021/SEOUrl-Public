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
import seourl.pack.SogouDomainPack;
import seourl.type.Filter;

/**
 *
 * @author yuri
 */
public class SogouDomainFilter extends DomainFilterAbstract {

    private int count = 0;

    public SogouDomainFilter(int pid,Filter filter, List<String> lkeyWords) {
        super(pid, filter, lkeyWords);
    }

    public SogouDomainPack getSSP() {
        return (SogouDomainPack) this.getSep();
    }

    @Override
    final protected String getPageUrl(String url, int i) {
        if (count > 10) {
            this.webClient.getCookieManager().clearCookies();
            count = 0;
        }

        String sPage = "";
        if (i > 1) {
            sPage = "&page=" + String.valueOf(i);
        }
        return String.format(Configure.SOGOU_DOMAIN + "\"%s\"%s", url, sPage);
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
    final protected void createNewSearchEnginePack(String url) {
        this.sep = new SogouDomainPack(url);
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
