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

/**
 *
 * @author Yuri
 */
public class So360SiteController extends Thread {

    private Date startTime;
    private UrlDataSet dataSet;
    private List<String> keywords;
    @Getter
    private Map<String, So360SitePack> mSDP = new TreeMap<>();
    private final int pid;

    public So360SiteController(int pid, Date startTime, UrlDataSet dataSet, List<String> keywords) {
        this.pid = pid;
        this.dataSet = dataSet;
        this.keywords = keywords;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        So360SiteFilter s = new So360SiteFilter(keywords);
        s.setCookiePath("cache/360SO-Site/");
        s.setCookie(pid + "-cookie.bin");
        s.loadCookie();
        String url;
        while (dataSet.hasNext()) {
            url = dataSet.getNext();
            s.doAnalysis(url);
            mSDP.put(url, s.getSSP());
            s.getSSP().saveFile();
        }
        s.saveCookie();
        s.close();
    }
}
