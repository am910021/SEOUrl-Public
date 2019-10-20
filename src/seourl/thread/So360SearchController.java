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
import seourl.filter.So360SearchFilter;
import seourl.pack.So360SerachPack;

/**
 *
 * @author Yuri
 */
public class So360SearchController extends Thread {

    private Date startTime;
    private List<String> urls;
    private List<String> keywords;
    @Getter
    private Map<String, So360SerachPack> mSDP = new HashMap<>();
    private final int pid;

    public So360SearchController(int pid, Date startTime, List<String> urls, List<String> keywords) {
        this.pid = pid;
        this.urls = urls;
        this.keywords = keywords;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        So360SearchFilter s = new So360SearchFilter(keywords);
        s.setCookiePath("cache/360SO-Search/");
        s.setCookie(pid + "-cookie.bin");
        s.loadCookie();
        for (String url : urls) {
            s.doAnalysis(url);
            mSDP.put(url, s.getSSP());
            s.getSSP().saveFile(url, startTime);
        }
        s.saveCookie();
        s.close();
    }
}
