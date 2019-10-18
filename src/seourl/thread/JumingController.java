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
public class JumingController extends ThreadAbstract{

    private List<String> urls;
    @Getter
    private Map<String, JumingPack> mJP = new HashMap<>();

    public JumingController(List<String> urls) {
        this.urls = urls;
    }
    
    @Override
    public void run() {
        JumingFilter j = new JumingFilter();
        j.loadWeb("http://www.juming.com");
        j.login();
        for (String url : urls) {
            j.doAnalysis(url);
            mJP.put(url, j.getJP());
        }
        j.close();
    }
}
