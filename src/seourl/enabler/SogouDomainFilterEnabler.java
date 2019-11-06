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
import seourl.thread.SogouDomainController;

/**
 *
 * @author Yuri
 */
public class SogouDomainFilterEnabler extends EnablerAbstract {

    private Map<Integer, SogouDomainController> sogoDomainCMap = new HashMap<>();

    private SogouDomainFilterEnabler() {
        Tools.checkKeyWordFile("SOGOU_DOMAIN.txt");
    }

    public static SogouDomainFilterEnabler getInstance() {
        return SogouDomainFilterEnablerHolder.INSTANCE;
    }

    @Override
    public void run() {
        SogouDomainController sdc;
        int maxThread = Math.min(Configure.MAX_THREAD, dsa.getSize());
        for (int i = 0; i < maxThread; i++) {
            sdc = new SogouDomainController(i, (UrlDataSet) dsa, Tools.loadKeyword("SOGOU_DOMAIN.txt"));
            sogoDomainCMap.put(i, sdc);
            sdc.start();
            Tools.sleep(1 * 1000, 5 * 1000);
        }
        sdc = null;

        for (Map.Entry<Integer, SogouDomainController> map : sogoDomainCMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.packMap.putAll(map.getValue().getPackMap());
        }
        sogoDomainCMap = null;
        if (Configure.DEBUG) {
            for (Map.Entry<String, PackAbstract> map : packMap.entrySet()) {
                map.getValue().print(map.getKey());
            }
        }
    }

    private static class SogouDomainFilterEnablerHolder {

        private static final SogouDomainFilterEnabler INSTANCE = new SogouDomainFilterEnabler();
    }
}
