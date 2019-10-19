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
import seourl.filter.SogouDomainFilter;
import seourl.pack.SogouDomainPack;

/**
 *
 * @author Yuri
 */
public class SogouDomainController extends Thread {

    private List<String> urls;
    private List<String> keywords;
    @Getter
    private Map<String, SogouDomainPack> mSDP = new HashMap<>();
    private final int pid;

    public SogouDomainController(int pid, List<String> urls, List<String> keywords) {
        this.pid = pid;
        this.urls = urls;
        this.keywords = keywords;
    }

    @Override
    public void run() {
        SogouDomainFilter s = new SogouDomainFilter(keywords);
        s.setCookiePath("cache/SogouDomain/");
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
