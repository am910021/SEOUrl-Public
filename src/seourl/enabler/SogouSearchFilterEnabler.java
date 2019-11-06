/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.enabler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import seourl.SEOUrl;
import seourl.data.UrlDataSet;
import seourl.enabler.ex.EnablerAbstract;
import seourl.other.Configure;
import seourl.other.Tools;
import seourl.pack.ex.PackAbstract;
import seourl.thread.SogouSearchController;

/**
 *
 * @author Yuri
 */
public class SogouSearchFilterEnabler extends EnablerAbstract {

    private Map<Integer, SogouSearchController> sogouSearchCMap = new HashMap<>();

    private SogouSearchFilterEnabler() {
    }

    public static SogouSearchFilterEnabler getInstance() {
        return SogouSearchFilterEnablerHolder.INSTANCE;
    }

    @Override
    public void run() {
        SogouSearchController ssc;
        int maxThread = Math.min(Configure.MAX_THREAD, dsa.getSize());
        for (int i = 0; i < maxThread; i++) {
            ssc = new SogouSearchController(i, (UrlDataSet) dsa);
            sogouSearchCMap.put(i, ssc);
            ssc.start();
            Tools.sleep(1 * 1000, 5 * 1000);
        }
        ssc = null;
        for (Map.Entry<Integer, SogouSearchController> map : sogouSearchCMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.packMap.putAll(map.getValue().getPackMap());
        }
        sogouSearchCMap = null;
        if (Configure.DEBUG) {
            for (Map.Entry<String, PackAbstract> map : packMap.entrySet()) {
                map.getValue().print(map.getKey());
            }
        }
    }

    private static class SogouSearchFilterEnablerHolder {

        private static final SogouSearchFilterEnabler INSTANCE = new SogouSearchFilterEnabler();
    }
}
