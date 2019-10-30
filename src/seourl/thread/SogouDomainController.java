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
import seourl.data.UrlDataSet;
import seourl.data.ex.DataSetAbstract;
import seourl.filter.SogouDomainFilter;
import seourl.pack.SogouDomainPack;

/**
 *
 * @author Yuri
 */
public class SogouDomainController extends Thread {

    private Date startTime;
    private UrlDataSet dataSet;
    private List<String> keywords;
    @Getter
    private Map<String, SogouDomainPack> mSDP = new HashMap<>();
    private final int pid;

    public SogouDomainController(int pid, Date startTime, UrlDataSet dataSet, List<String> keywords) {
        this.pid = pid;
        this.dataSet = dataSet;
        this.keywords = keywords;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        SogouDomainFilter s = new SogouDomainFilter(keywords);
        s.setCookiePath("cache/SogouDomain/");
        s.setCookie(pid + "-cookie.bin");
        s.loadCookie();
        String url;
        while (dataSet.hasNextUrl()) {
            url = dataSet.getNextUrl();
            s.doAnalysis(url);
            mSDP.put(url, s.getSSP());
            s.getSSP().saveFile(url, startTime);
        }
        s.saveCookie();
        s.close();
    }
}
