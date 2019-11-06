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
import seourl.thread.So360SiteController;

/**
 *
 * @author Yuri
 */
public class So360SiteFilterEnabler extends EnablerAbstract {

    private Map<Integer, So360SiteController> so360SiteCMap = new HashMap<>();

    private So360SiteFilterEnabler() {
         Tools.checkKeyWordFile("SO360_SITE.txt");
    }

    public static So360SiteFilterEnabler getInstance() {
        return So360SiteFilterHolder.INSTANCE;
    }

    @Override
    public void run() {
        So360SiteController ssc;
        int maxThread = Math.min(Configure.MAX_THREAD, dsa.getSize());
        for (int i = 0; i < maxThread; i++) {
            ssc = new So360SiteController(i, (UrlDataSet) dsa, Tools.loadKeyword("SO360_SITE.txt"));
            so360SiteCMap.put(i, ssc);
            ssc.start();
            Tools.sleep(1 * 1000, 5 * 1000);
        }

        ssc = null;
        for (Map.Entry<Integer, So360SiteController> map : so360SiteCMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.packMap.putAll(map.getValue().getPackMap());
        }
        so360SiteCMap = null;
        if (Configure.DEBUG) {
            for (Map.Entry<String, PackAbstract> map : packMap.entrySet()) {
                map.getValue().print(map.getKey());
            }
        }
    }

    private static class So360SiteFilterHolder {

        private static final So360SiteFilterEnabler INSTANCE = new So360SiteFilterEnabler();
    }
}
