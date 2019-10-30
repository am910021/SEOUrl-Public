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
import seourl.data.UrlDataSet;
import seourl.data.ex.DataSetAbstract;
import seourl.filter.WebArchiveSnapsHot;
import seourl.pack.WebArchivePack;

/**
 *
 * @author Yuri
 */
public class WebArchiveSnapsHotsController extends Thread {

    private Date startTime;
    @Getter
    private Map<String, WebArchivePack> mWAP = new HashMap<>();
    private UrlDataSet dataSet;
    private final int pid;

    public WebArchiveSnapsHotsController(int pid, Date startTime, UrlDataSet dataSet) {
        this.pid = pid;
        this.startTime = startTime;
        this.dataSet = dataSet;
    }

    @Override
    public void run() {
        WebArchiveSnapsHot wash = new WebArchiveSnapsHot(pid);
        String url;
        while (dataSet.hasNextUrl()) {
            url = dataSet.getNextUrl();
            wash.doAnalysis(url);
            mWAP.put(url, wash.getWap());
            if (Configure.WEBARCHIVE_MODE != 1) {
                wash.getWap().saveFile(url, startTime);
            }

        }
    }
}
