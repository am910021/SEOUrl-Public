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
import seourl.filter.So360SearchFilter;
import seourl.filter.SogouDomainFilter;
import seourl.pack.SearchEnginePack;

/**
 *
 * @author Yuri
 */
public class So360SearchController extends Thread {

    private List<String> urls;
    private List<String> keywords;
    @Getter
    private Map<String, SearchEnginePack> mSDP = new HashMap<>();
    private final int pid;

    public So360SearchController(int pid, List<String> urls, List<String> keywords) {
        this.pid = pid;
        this.urls = urls;
        this.keywords = keywords;
    }

    @Override
    public void run() {
        So360SearchFilter s = new So360SearchFilter(keywords);
        s.setCookiePath("cache/360SO-Search/");
        s.setCookie(pid + "-cookie.bin");
        s.loadCookie();
        for (String url : urls) {
            s.doAnalysis(url);
            mSDP.put(url, s.getSep());
        }
        s.saveCookie();
        s.close();
    }
}
