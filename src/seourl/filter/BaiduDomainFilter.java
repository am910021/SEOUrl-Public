/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter;

import com.gargoylesoftware.htmlunit.html.DomElement;
import java.util.List;
import seourl.filter.ex.DomainFilterAbstract;
import seourl.filter.ex.FilterAbstract;

/**
 *
 * @author yuri
 */
public class BaiduDomainFilter extends DomainFilterAbstract {

    public BaiduDomainFilter(List<String> lkeyWords) {
        super("Baidu-Domain", lkeyWords);
    }

    @Override
    final protected String getPageUrl(String url, int i) {
        String sPage = "";
        if (i > 1) {
            sPage = "&pn=" + String.valueOf(i * 10);
        }
        return String.format("https://www.baidu.com/s?wd=domain:%s", url, sPage);
    }

    @Override
    protected List<DomElement> getResultList() {
        List<DomElement> list = page.getByXPath("//div[@class='result c-container ']");

        return list;
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

}
