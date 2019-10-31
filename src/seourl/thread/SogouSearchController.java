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
import seourl.filter.SogouSearchFilter;
import seourl.pack.SogouSerachPack;

/**
 *
 * @author Yuri
 */
public class SogouSearchController extends Thread {

    private Date startTime;
    private UrlDataSet dataSet;
    private List<String> keywords;
    @Getter
    private Map<String, SogouSerachPack> mSDP = new TreeMap<>();
    private final int pid;

    public SogouSearchController(int pid, Date startTime, UrlDataSet dataSet, List<String> keywords) {
        this.pid = pid;
        this.dataSet = dataSet;
        this.keywords = keywords;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        SogouSearchFilter s = new SogouSearchFilter(keywords);
        s.setCookiePath("cache/Sogou-Search/");
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
