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
import seourl.filter.SogouDomainFilter;
import seourl.pack.JumingPack;
import seourl.pack.DomainPack;

/**
 *
 * @author Yuri
 */
public class SogouDomainController extends Thread{

    private List<String> urls;
    @Getter
    private Map<String, DomainPack> mSDP = new HashMap<>();

    public SogouDomainController(List<String> urls) {
        this.urls = urls;
    }
    
    @Override
    public void run() {
        SogouDomainFilter s = new SogouDomainFilter();
        for (String url : urls) {
            s.doAnalysis(url);
            mSDP.put(url, s.getSdp());
        }
        s.close();
    }
}
