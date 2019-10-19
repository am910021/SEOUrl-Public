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
import seourl.filter.JumingFilter;
import seourl.pack.JumingPack;

/**
 *
 * @author Yuri
 */
public class JumingController extends Thread {

    private List<String> urls;
    @Getter
    private Map<String, JumingPack> mJP = new HashMap<>();
    private final int pid;

    public JumingController(int pid, List<String> urls) {
        this.pid = pid;
        this.urls = urls;
    }

    @Override
    public void run() {
        JumingFilter j = new JumingFilter();
        j.setCookiePath("cache/Juming/");
        j.setCookie(pid + "-cookie.bin");
        j.loadCookie();
        j.loadWeb("http://www.juming.com");
        j.login();
        j.saveCookie();
        for (String url : urls) {
            j.doAnalysis(url);
            mJP.put(url, j.getJp());
        }
        j.saveCookie();
        j.close();
    }
}
