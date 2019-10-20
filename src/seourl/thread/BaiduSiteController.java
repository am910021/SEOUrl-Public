/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import seourl.filter.BaiduSiteFilter;
import seourl.pack.BaiduSitePack;

/**
 *
 * @author Yuri
 */
public class BaiduSiteController extends Thread {

    private Date startTime;
    private List<String> urls;
    private List<String> keywords;
    @Getter
    private Map<String, BaiduSitePack> mSDP = new HashMap<>();
    private final int pid;

    public BaiduSiteController(int pid, Date startTime, List<String> urls, List<String> keywords) {
        this.pid = pid;
        this.urls = urls;
        this.keywords = keywords;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        BaiduSiteFilter s = new BaiduSiteFilter(keywords);
        s.setCookiePath("cache/Baidu-Site/");
        s.setCookie(pid + "-cookie.bin");
        s.loadCookie();
        for (String url : urls) {
            s.doAnalysis(url);
            mSDP.put(url, s.getBSP());
            s.getBSP().saveFile(url, startTime);
        }
        s.saveCookie();
        s.close();
    }
}
