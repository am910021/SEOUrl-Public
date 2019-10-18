/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter;

import com.gargoylesoftware.htmlunit.html.DomElement;
import java.util.List;
import seourl.filter.ex.DomainFilterAbstract;

/**
 *
 * @author yuri
 */
public class SogouDomainFilter extends DomainFilterAbstract {

    public SogouDomainFilter(List<String> lkeyWords) {
        super("Sogou-domain", lkeyWords);
    }

    @Override
    final protected String getPageUrl(String url, int i) {
        String sPage = "";
        if (i > 1) {
            sPage = "&page=" + String.valueOf(i);
        }
        return String.format("https://www.sogou.com/web?query=\"%s\"%s", url, sPage);
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

}
