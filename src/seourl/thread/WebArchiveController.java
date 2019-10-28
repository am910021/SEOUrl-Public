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
import seourl.filter.WebArchiveFilter;
import seourl.pack.WebArchivePack;

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
    private Map<String, WebArchivePack> mWAP = new HashMap<>();
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
        WebArchiveFilter waf2 = new WebArchiveFilter(titleKeywords, contentKeywords);
        for (String url : urls) {
            waf2.doAnalysis(url);
            mWAP.put(url, waf2.getWap());
            waf2.getWap().saveFile(url, startTime);
        }
        //waf2.close();
    }
}
