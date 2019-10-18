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
import seourl.pack.SearchEnginePack;

/**
 *
 * @author Yuri
 */
public class BaiduDomainController extends Thread {

    private List<String> urls;
    private List<String> keywords;
    @Getter
    private Map<String, SearchEnginePack> mDP = new HashMap<>();

    public BaiduDomainController(List<String> urls, List<String> keywords) {
        this.urls = urls;
        this.keywords = keywords;
    }

    @Override
    public void run() {
        BaiduDomainFilter s = new BaiduDomainFilter(keywords);
        for (String url : urls) {
            s.doAnalysis(url);
            mDP.put(url, s.getSep());
        }
        s.close();
    }
}
