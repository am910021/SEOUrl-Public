/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread;

import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import seourl.Configure;
import seourl.data.UrlDataSet;
import seourl.filter.WebArchiveSnapsHot;
import seourl.pack.WebArchivePack;
import seourl.thread.ex.ControllerAbstract;
import seourl.type.Filter;

/**
 *
 * @author Yuri
 */
public class WebArchiveSnapsHotsController extends ControllerAbstract {

    @Getter
    private Map<String, WebArchivePack> mWAP = new TreeMap<>();
    private final int pid;

    public WebArchiveSnapsHotsController(int pid, UrlDataSet dataSet) {
        super(Filter.WEB_ARCHIVE_LIST, dataSet);
        this.pid = pid;
    }

    @Override
    public void run() {
        WebArchiveSnapsHot wash = new WebArchiveSnapsHot(pid);
        String url;
        while (dsa.hasNext()) {
            url = ((UrlDataSet) dsa).getNext();
            wash.doAnalysis(url);
            mWAP.put(url, wash.getWap());
            if (!Configure.WEBARCHIVE_TITLE_FILTER && !Configure.WEBARCHIVE_CONTENT_FILTER) {
                wash.getWap().saveFile();
                this.printProgress();
            }
        }
    }
}
