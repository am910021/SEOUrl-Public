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
import seourl.pack.DomainPack;

/**
 *
 * @author Yuri
 */
public class BaiduDomainController extends Thread{

    private List<String> urls;
    @Getter
    private Map<String, DomainPack> mDP = new HashMap<>();
    public BaiduDomainController(List<String> urls) {
        this.urls = urls;
    }
    
    @Override
    public void run() {
        BaiduDomainFilter s = new BaiduDomainFilter();
        for (String url : urls) {
            s.doAnalysis(url);
            mDP.put(url, s.getDp());
        }
        s.close();
    }
}
