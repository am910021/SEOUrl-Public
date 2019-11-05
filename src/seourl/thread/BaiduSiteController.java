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
import seourl.filter.BaiduSiteFilter;
import seourl.pack.BaiduSitePack;

/**
 *
 * @author Yuri
 */
public class BaiduSiteController extends Thread {

    private UrlDataSet dataSet;
    private List<String> keywords;
    @Getter
    private Map<String, BaiduSitePack> mSDP = new TreeMap<>();
    private final int pid;

    public BaiduSiteController(int pid, UrlDataSet dataSet, List<String> keywords) {
        this.pid = pid;
        this.dataSet = dataSet;
        this.keywords = keywords;
    }

    @Override
    public void run() {
        BaiduSiteFilter s = new BaiduSiteFilter(keywords);
        s.setCookiePath("cache/Baidu-Site/");
        s.setCookie(pid + "-cookie.bin");
        s.loadCookie();
        String url;
        while (dataSet.hasNext()) {
            url = dataSet.getNext();
            s.doAnalysis(url);
            mSDP.put(url, s.getBSP());
            s.getBSP().saveFile();
        }
        s.saveCookie();
        s.close();
    }
}
