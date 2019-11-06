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
import seourl.thread.So360SearchController;

/**
 *
 * @author Yuri
 */
public class So360SearchFilterEnabler extends EnablerAbstract {

    private Map<Integer, So360SearchController> so360SearchMap = new HashMap<>();

    private So360SearchFilterEnabler() {
    }

    public static So360SearchFilterEnabler getInstance() {
        return So360SearchFilterHolder.INSTANCE;
    }

    @Override
    public void run() {
        So360SearchController ssc;
        int maxThread = Math.min(Configure.MAX_THREAD, dsa.getSize());
        for (int i = 0; i < maxThread; i++) {
            ssc = new So360SearchController(i, (UrlDataSet) dsa);
            so360SearchMap.put(i, ssc);
            ssc.start();
            Tools.sleep(1 * 1000, 5 * 1000);
        }
        ssc = null;
        for (Map.Entry<Integer, So360SearchController> map : so360SearchMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.packMap.putAll(map.getValue().getPackMap());
        }
        so360SearchMap = null;
        if (Configure.DEBUG) {
            for (Map.Entry<String, PackAbstract> map : packMap.entrySet()) {
                map.getValue().print(map.getKey());
            }
        }
    }

    private static class So360SearchFilterHolder {

        private static final So360SearchFilterEnabler INSTANCE = new So360SearchFilterEnabler();
    }
}
