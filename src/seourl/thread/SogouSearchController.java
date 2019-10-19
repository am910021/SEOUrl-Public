/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import seourl.filter.SogouSearchFilter;
import seourl.pack.SogouSerachPack;

/**
 *
 * @author Yuri
 */
public class SogouSearchController extends Thread {

    private List<String> urls;
    private List<String> keywords;
    @Getter
    private Map<String, SogouSerachPack> mSDP = new HashMap<>();
    private final int pid;

    public SogouSearchController(int pid, List<String> urls, List<String> keywords) {
        this.pid = pid;
        this.urls = urls;
        this.keywords = keywords;
    }

    @Override
    public void run() {
        SogouSearchFilter s = new SogouSearchFilter(keywords);
        s.setCookiePath("cache/Sogou-Search/");
        s.setCookie(pid + "-cookie.bin");
        s.loadCookie();
        for (String url : urls) {
            s.doAnalysis(url);
            mSDP.put(url, s.getSSP());
        }
        s.saveCookie();
        s.close();
    }
}
