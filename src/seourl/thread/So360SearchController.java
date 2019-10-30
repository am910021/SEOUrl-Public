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
import seourl.filter.So360SearchFilter;
import seourl.pack.So360SerachPack;

/**
 *
 * @author Yuri
 */
public class So360SearchController extends Thread {

    private Date startTime;
    private UrlDataSet dataSet;
    private List<String> keywords;
    @Getter
    private Map<String, So360SerachPack> mSDP = new HashMap<>();
    private final int pid;

    public So360SearchController(int pid, Date startTime, UrlDataSet dataSet, List<String> keywords) {
        this.pid = pid;
        this.dataSet = dataSet;
        this.keywords = keywords;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        So360SearchFilter s = new So360SearchFilter(keywords);
        s.setCookiePath("cache/360SO-Search/");
        s.setCookie(pid + "-cookie.bin");
        s.loadCookie();
        String url;
        while (dataSet.hasNextUrl()) {
            url = dataSet.getNextUrl();
            s.doAnalysis(url);
            mSDP.put(url, s.getSSP());
            s.getSSP().saveFile(url, startTime);
        }
        s.saveCookie();
        s.close();
    }
}
