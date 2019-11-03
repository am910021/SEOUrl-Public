/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.enabler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import seourl.other.Configure;
import seourl.SEOUrl;
import seourl.other.TPair;
import seourl.other.Tools;
import seourl.data.SnapsHotsDataSet;
import seourl.data.UrlDataSet;
import seourl.enabler.ex.EnablerAbstract;
import seourl.pack.WebArchivePack;
import seourl.thread.WebArchiveController;
import seourl.thread.WebArchiveSnapsHotsController;

/**
 *
 * @author Yuri
 */
public class WebArchiveFilterEnabler extends EnablerAbstract {

    @Getter
    private Map<String, WebArchivePack> wap2Map = new TreeMap<>();   //WebArchive資料集

    private Map<Integer, WebArchiveSnapsHotsController> washcMap = new HashMap<>();
    private Map<Integer, WebArchiveController> wacMap = new HashMap<>();

    private WebArchiveFilterEnabler() {
    }

    public static WebArchiveFilterEnabler getInstance() {
        return WebArchiveFilterEnablerHolder.INSTANCE;
    }

    @Override
    public void run() {
        if (!Configure.ENABLE_WEBARCHIVE) {
            return;
        }
        Tools.checkKeyWordFile("WEBARCHIVE-TITLE.txt");
        Tools.checkKeyWordFile("WEBARCHIVE-CONTENT.txt");

        WebArchiveSnapsHotsController washc;
        int maxThread = Math.min(Configure.WEBARCHIVE_MAX_THREAD, dsa.getSize());
        for (int i = 0; i < maxThread; i++) {
            washc = new WebArchiveSnapsHotsController(i, (UrlDataSet) dsa);
            washcMap.put(i, washc);
            washc.start();
            Tools.sleep(1 * 1000, 5 * 1000);
        }

        washc = null;
        for (Map.Entry<Integer, WebArchiveSnapsHotsController> map : washcMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Tools.printError(this.getName(), ex);
            }
            this.wap2Map.putAll(map.getValue().getMWAP());
        }
        washcMap = null;
        if (Configure.DEBUG) {
            for (Map.Entry<String, WebArchivePack> map : wap2Map.entrySet()) {
                map.getValue().print(map.getKey());
            }
        }

        long total = (System.currentTimeMillis() - Configure.startTime.getTime());
        long h = TimeUnit.MILLISECONDS.toHours(total);
        long m = TimeUnit.MILLISECONDS.toMinutes(total) - (h * 60);
        long s = TimeUnit.MILLISECONDS.toSeconds(total) - ((h * 60) + m) * 60;
        System.out.printf("快照列表讀取時間:%d小時 %d分鐘 %d秒\n", h, m, s);

        if (!Configure.WEBARCHIVE_TITLE_FILTER && !Configure.WEBARCHIVE_CONTENT_FILTER) {
            return;
        }

        SnapsHotsDataSet snapsHotsDataSet = new SnapsHotsDataSet();
        snapsHotsDataSet.setData(getSplitSnapsHots());
        int totalSnapsHotsSize = snapsHotsDataSet.getSize();
        List<String> title = Tools.loadKeyword("WEBARCHIVE-TITLE.txt");;
        List<String> content = Tools.loadKeyword("WEBARCHIVE-CONTENT.txt");
        System.out.printf("URL數量:%d  預計讀取快照量:%d\n", dsa.getSize(), totalSnapsHotsSize);
        WebArchiveController wac;

        for (int i = 0; i < Configure.WEBARCHIVE_MAX_THREAD; i++) {
            wac = new WebArchiveController(i, wap2Map, snapsHotsDataSet, title, content);
            wacMap.put(i, wac);
            wac.start();
            Tools.sleep(1 * 1000, 5 * 1000);
        }

        wac = null;

        for (Map.Entry<Integer, WebArchiveController> map : wacMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        wacMap = null;
        if (Configure.DEBUG) {
            for (Map.Entry<String, WebArchivePack> map : wap2Map.entrySet()) {
                map.getValue().print(map.getKey());
                map.getValue().saveFile();
            }
        }
        total = (System.currentTimeMillis() - Configure.startTime.getTime());
        h = TimeUnit.MILLISECONDS.toHours(total);
        m = TimeUnit.MILLISECONDS.toMinutes(total) - (h * 60);
        s = TimeUnit.MILLISECONDS.toSeconds(total) - ((h * 60) + m) * 60;
        System.out.printf("快照讀取時間:%d小時 %d分鐘 %d秒\n", h, m, s);
    }

    private List<TPair<String, Integer, Long>> getSplitSnapsHots() {
        List<TPair<String, Integer, Long>> ltp = new ArrayList<>();
        TPair<String, Integer, Long> tp;
        for (Map.Entry<String, WebArchivePack> map : wap2Map.entrySet()) {
            for (Map.Entry<Integer, List<Long>> snapshot : map.getValue().getSnapshots().entrySet()) {
                for (Long l : snapshot.getValue()) {
                    tp = new TPair<>(map.getKey(), snapshot.getKey(), l);
                    ltp.add(tp);
                }
            }
        }
        return ltp;
    }

    private static class WebArchiveFilterEnablerHolder {

        private static final WebArchiveFilterEnabler INSTANCE = new WebArchiveFilterEnabler();
    }
}
