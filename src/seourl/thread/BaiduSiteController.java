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
import seourl.data.UrlDataSet;
import seourl.data.ex.DataSetAbstract;
import seourl.filter.BaiduSiteFilter;
import seourl.pack.BaiduSitePack;

/**
 *
 * @author Yuri
 */
public class BaiduSiteController extends Thread {

    private Date startTime;
    private UrlDataSet dataSet;
    private List<String> keywords;
    @Getter
    private Map<String, BaiduSitePack> mSDP = new HashMap<>();
    private final int pid;

    public BaiduSiteController(int pid, Date startTime, UrlDataSet dataSet, List<String> keywords) {
        this.pid = pid;
        this.dataSet = dataSet;
        this.keywords = keywords;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        BaiduSiteFilter s = new BaiduSiteFilter(keywords);
        s.setCookiePath("cache/Baidu-Site/");
        s.setCookie(pid + "-cookie.bin");
        s.loadCookie();
        String url;
        while (dataSet.hasNextUrl()) {
            url = dataSet.getNextUrl();
            s.doAnalysis(url);
            mSDP.put(url, s.getBSP());
            s.getBSP().saveFile(url, startTime);
        }
        s.saveCookie();
        s.close();
    }
}
