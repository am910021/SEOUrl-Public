/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import seourl.Tools;
import seourl.filter.BaiduDomainFilter;
import seourl.filter.WebArchiveFilter2;
import seourl.pack.BaiduDomainPack;
import seourl.pack.WebArchivePack2;

/**
 *
 * @author Yuri
 */
public class WebArchiveController extends Thread {

    private Date startTime;
    private List<String> urls;
    private List<String> titleKeywords;
    private List<String> contentKeywords;
    @Getter
    private Map<String, WebArchivePack2> mWAP = new HashMap<>();
    private final int pid;
    
    
    public WebArchiveController(int pid, Date startTime, List<String> urls, List<String> titleKeywords, List<String> contentKeywords) {
        this.pid = pid;
        this.urls = urls;
        this.titleKeywords = titleKeywords;
        this.contentKeywords = contentKeywords;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        WebArchiveFilter2 waf2 = new WebArchiveFilter2(titleKeywords, contentKeywords);
        for (String url : urls) {
            waf2.doAnalysis(url);
            waf2.getWap().print(url);
            mWAP.put(url, waf2.getWap());
            
        }
        waf2.close();
    }
}
