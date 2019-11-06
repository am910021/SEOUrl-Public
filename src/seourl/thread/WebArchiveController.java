/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread;

import java.util.List;
import java.util.Map;
import seourl.other.TPair;
import seourl.other.Tools;
import seourl.data.SnapsHotsDataSet;
import seourl.filter.WebArchiveFilter;
import seourl.pack.WebArchivePack;
import seourl.pack.ex.PackAbstract;
import seourl.thread.ex.ControllerAbstract;
import seourl.type.Filter;

/**
 *
 * @author Yuri
 */
public class WebArchiveController extends ControllerAbstract<SnapsHotsDataSet> {

    private final List<String> titleKeywords;
    private final List<String> contentKeywords;

    public WebArchiveController(int pid, Map<String, PackAbstract> mWAP, SnapsHotsDataSet dataSet, List<String> titleKeywords, List<String> contentKeywords) {
        super(pid, Filter.WEB_ARCHIVE, dataSet, mWAP);
        this.titleKeywords = titleKeywords;
        this.contentKeywords = contentKeywords;
    }

    @Override
    public void run() {
        WebArchiveFilter waf3 = new WebArchiveFilter(pid, filter, titleKeywords, contentKeywords);
        TPair<String, Integer, Long> tp;
        WebArchivePack wap;
        String url;

        while (dsa.hasNext()) {
            tp = dsa.getNext();
            url = tp.getLeft();
            wap = (WebArchivePack) packMap.get(url);
            waf3.setWap(wap);
            waf3.doAnalysis(url, tp.getRight());
            this.printProgress();
            //wap.saveFile(url, startTime);
            Tools.sleep(2 * 1000, 7 * 1000);
        }
    }

}
