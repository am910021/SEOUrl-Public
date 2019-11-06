/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import seourl.data.UrlDataSet;
import seourl.filter.So360SiteFilter;
import seourl.pack.So360SitePack;
import seourl.thread.ex.ControllerAbstract;
import seourl.type.Filter;

/**
 *
 * @author Yuri
 */
public class So360SiteController extends ControllerAbstract<UrlDataSet> {

    private List<String> keywords;
    @Getter
    private Map<String, So360SitePack> mSDP = new TreeMap<>();

    public So360SiteController(int pid, UrlDataSet dataSet, List<String> keywords) {
        super(pid, Filter.SO360_SITE, dataSet);
        this.keywords = keywords;
    }

    @Override
    public void run() {
        So360SiteFilter s = new So360SiteFilter(pid, keywords);
        s.setCookiePath("cache/360SO-Site/");
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
