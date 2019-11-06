/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread;

import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import seourl.other.Configure;
import seourl.data.UrlDataSet;
import seourl.filter.WebArchiveSnapsHot;
import seourl.pack.WebArchivePack;
import seourl.thread.ex.ControllerAbstract;
import seourl.type.Filter;

/**
 *
 * @author Yuri
 */
public class WebArchiveSnapsHotsController extends ControllerAbstract<UrlDataSet> {

    @Getter
    private Map<String, WebArchivePack> mWAP = new TreeMap<>();

    public WebArchiveSnapsHotsController(int pid, UrlDataSet dataSet) {
        super(pid, Filter.WEB_ARCHIVE_LIST, dataSet);
    }

    @Override
    public void run() {
        WebArchiveSnapsHot wash = new WebArchiveSnapsHot(pid);
        String url;
        while (dsa.hasNext()) {
            url = dsa.getNext();
            wash.doAnalysis(url);
            mWAP.put(url, wash.getWap());
            this.printProgress();
            if (!Configure.WEBARCHIVE_TITLE_FILTER && !Configure.WEBARCHIVE_CONTENT_FILTER) {
                wash.getWap().saveFile();
            }
        }
    }
}
