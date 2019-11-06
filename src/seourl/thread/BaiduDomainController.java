/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import seourl.data.UrlDataSet;
import seourl.filter.BaiduDomainFilter;
import seourl.pack.BaiduDomainPack;
import seourl.thread.ex.ControllerAbstract;
import seourl.type.Filter;

/**
 *
 * @author Yuri
 */
public class BaiduDomainController extends ControllerAbstract<UrlDataSet> {

    private List<String> keywords;
    @Getter
    private Map<String, BaiduDomainPack> mDP = new TreeMap<>();

    public BaiduDomainController(int pid, UrlDataSet dataSet, List<String> keywords) {
        super(pid, Filter.BAIDU_DOMAIN, dataSet);
        this.keywords = keywords;
    }

    @Override
    public void run() {
        BaiduDomainFilter s = new BaiduDomainFilter(pid,keywords);
        s.setCookiePath("cache/BaiduDomain/");
        s.setCookie(pid + "-cookie.bin");
        s.loadCookie();
        String url;
        while (dsa.hasNext()) {
            url = dsa.getNext();
            s.doAnalysis(url);
            mDP.put(url, s.getBDP());
            s.getBDP().saveFile();
        }
        s.saveCookie();
        s.close();
    }
}
