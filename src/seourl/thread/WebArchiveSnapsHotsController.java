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
import seourl.Configure;
import seourl.filter.WebArchiveFilter;
import seourl.filter.WebArchiveSnapsHot;
import seourl.pack.WebArchivePack;

/**
 *
 * @author Yuri
 */
public class WebArchiveSnapsHotsController extends Thread {
    
    private Date startTime;
    private List<String> urls;
    @Getter
    private Map<String, WebArchivePack> mWAP = new HashMap<>();
    private final int pid;
    
    public WebArchiveSnapsHotsController(int pid, Date startTime, List<String> urls) {
        this.pid = pid;
        this.urls = urls;
        this.startTime = startTime;
    }
    
    @Override
    public void run() {
        WebArchiveSnapsHot wash = new WebArchiveSnapsHot();
        for (String url : urls) {
            wash.doAnalysis(url);
            mWAP.put(url, wash.getWap());
        }
    }
}
