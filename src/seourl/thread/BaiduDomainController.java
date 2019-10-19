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
import seourl.filter.BaiduDomainFilter;
import seourl.pack.BaiduDomainPack;

/**
 *
 * @author Yuri
 */
public class BaiduDomainController extends Thread {

    private List<String> urls;
    private List<String> keywords;
    @Getter
    private Map<String, BaiduDomainPack> mDP = new HashMap<>();
    private final int pid;

    public BaiduDomainController(int pid, List<String> urls, List<String> keywords) {
        this.pid = pid;
        this.urls = urls;
        this.keywords = keywords;
    }

    @Override
    public void run() {
        BaiduDomainFilter s = new BaiduDomainFilter(keywords);
        s.setCookiePath("cache/BaiduDomain/");
        s.setCookie(pid + "-cookie.bin");
        s.loadCookie();
        for (String url : urls) {
            s.doAnalysis(url);
            mDP.put(url, s.getBDP());
        }
        s.saveCookie();
        s.close();
    }
}
