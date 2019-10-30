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
import seourl.TPair;
import seourl.Tools;
import seourl.data.SnapsHotsDataSet;
import seourl.filter.WebArchiveFilter3;
import seourl.pack.WebArchivePack;

/**
 *
 * @author Yuri
 */
public class WebArchiveController extends Thread {

    private Date startTime;
    private List<String> titleKeywords;
    private List<String> contentKeywords;
    @Getter
    private Map<String, WebArchivePack> mWAP = new HashMap<>();
    private final int pid;

    final SnapsHotsDataSet dataSet;

    public WebArchiveController(int pid, Date startTime, Map<String, WebArchivePack> mWAP, SnapsHotsDataSet dataSet, List<String> titleKeywords, List<String> contentKeywords) {
        this.pid = pid;
        this.titleKeywords = titleKeywords;
        this.contentKeywords = contentKeywords;
        this.dataSet = dataSet;
        this.mWAP = mWAP;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        WebArchiveFilter3 waf3 = new WebArchiveFilter3(pid, titleKeywords, contentKeywords);
        TPair<String, Integer, Long> tp;
        WebArchivePack wap;
        String url;
        while (dataSet.hasNextWASH()) {
            tp = dataSet.getNextWASH();
            url = tp.getLeft();
            wap = mWAP.get(url);
            waf3.setWap(wap);
            waf3.doAnalysis(url, tp.getRight());
            printProgress();
            //wap.saveFile(url, startTime);
            Tools.sleep(2 * 1000, 7 * 1000);
        }

        //waf2.close();
    }
    
    
    private void printProgress(){
        dataSet.addProgress();
        if(dataSet.isNeedPrintProgress()){
            System.out.printf("WebArchive執行進度 %d / %d \r\n", dataSet.getProgress(), dataSet.getWashSize());
        }
    }
}
