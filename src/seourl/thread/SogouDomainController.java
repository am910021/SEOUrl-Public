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
import seourl.pack.SearchEnginePack;

/**
 *
 * @author Yuri
 */
public class SogouDomainController extends Thread {

    private List<String> urls;
    private List<String> keywords;
    @Getter
    private Map<String, SearchEnginePack> mSDP = new HashMap<>();

    public SogouDomainController(List<String> urls, List<String> keywords) {
        this.urls = urls;
        this.keywords = keywords;
    }

    @Override
    public void run() {
        SogouDomainFilter s = new SogouDomainFilter(keywords);
        for (String url : urls) {
            s.doAnalysis(url);
            mSDP.put(url, s.getSep());
        }
        s.close();
    }
}
