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
import seourl.filter.SogouDomainFilter;
import seourl.pack.SogouDomainPack;
import seourl.thread.ex.ControllerAbstract;
import seourl.type.Filter;

/**
 *
 * @author Yuri
 */
public class SogouDomainController extends ControllerAbstract<UrlDataSet> {

    private List<String> keywords;
    @Getter
    private Map<String, SogouDomainPack> mSDP = new TreeMap<>();

    public SogouDomainController(int pid, UrlDataSet dataSet, List<String> keywords) {
        super(pid, Filter.SOGOU_DOMAIN, dataSet);
        this.keywords = keywords;

    }

    @Override
    public void run() {
        SogouDomainFilter s = new SogouDomainFilter(pid, keywords);
        s.setCookiePath("cache/SogouDomain/");
        s.setCookie(pid + "-cookie.bin");
        s.loadCookie();
        String url;
        while (dsa.hasNext()) {
            url = dsa.getNext();
            s.doAnalysis(url);
            mSDP.put(url, s.getSSP());
            s.getSSP().saveFile();
        }
        s.saveCookie();
        s.close();
    }
}
