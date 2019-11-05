/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.enabler;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import seourl.SEOUrl;
import seourl.data.UrlDataSet;
import seourl.enabler.ex.EnablerAbstract;
import seourl.other.Configure;
import seourl.other.Tools;
import seourl.pack.BaiduSitePack;
import seourl.pack.SogouDomainPack;
import seourl.pack.ex.PackAbstract;
import seourl.thread.BaiduSiteController;

/**
 *
 * @author Yuri
 */
public class BaiduSiteFilterEnabler extends EnablerAbstract {

    private Map<Integer, BaiduSiteController> baiduSiteCMap = new HashMap<>();

    private BaiduSiteFilterEnabler() {
        Tools.checkKeyWordFile("BAIDU_SITE.txt");
    }

    public static BaiduSiteFilterEnabler getInstance() {
        return BaiduSiteFilterEnablerHolder.INSTANCE;
    }

    @Override
    public void run() {
        BaiduSiteController bsc;
        int maxThread = Math.min(Configure.MAX_THREAD, dsa.getSize());
        for (int i = 0; i < maxThread; i++) {
            bsc = new BaiduSiteController(i, (UrlDataSet) dsa, Tools.loadKeyword("BAIDU_SITE.txt"));
            baiduSiteCMap.put(i, bsc);
            bsc.start();
            Tools.sleep(1 * 1000, 5 * 1000);
        }
        bsc = null;
        for (Map.Entry<Integer, BaiduSiteController> map : baiduSiteCMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.packMap.putAll(map.getValue().getMSDP());
        }
        baiduSiteCMap = null;
        if (Configure.DEBUG) {
            for (Map.Entry<String, PackAbstract> map : packMap.entrySet()) {
                map.getValue().print(map.getKey());
            }
        }
    }

    private static class BaiduSiteFilterEnablerHolder {

        private static final BaiduSiteFilterEnabler INSTANCE = new BaiduSiteFilterEnabler();
    }
}
