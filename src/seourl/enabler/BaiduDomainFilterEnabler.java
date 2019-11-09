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
import seourl.thread.BaiduDomainController;

/**
 *
 * @author Yuri
 */
public class BaiduDomainFilterEnabler<BaiduDomainPack> extends EnablerAbstract {

    private Map<Integer, BaiduDomainController> baiduDomainCMap = new HashMap<>();

    private BaiduDomainFilterEnabler() {
    }

    public static BaiduDomainFilterEnabler getInstance() {
        return BaiduDomainFilterEnablerHolder.INSTANCE;
    }

    @Override
    public void run() {
        if (dsa.getSize() == 0) {
            return;
        }
        
        BaiduDomainController bdc;
        int maxThread = Math.min(Configure.MAX_THREAD, dsa.getSize());
        for (int i = 0; i < maxThread; i++) {
            bdc = new BaiduDomainController(i, (UrlDataSet) dsa);
            baiduDomainCMap.put(i, bdc);
            bdc.start();
            Tools.sleep(1 * 1000, 5 * 1000);
        }
        bdc = null;
//        while (true) {
//            for (Map.Entry<Integer, BaiduDomainController> map : baiduDomainCMap.entrySet()) {
//                System.out.println(map.getValue().isAlive());
//            }
//
//            if (0 > 1) {
//                break;
//            }
//        }

        for (Map.Entry<Integer, BaiduDomainController> map : baiduDomainCMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.packMap.putAll(map.getValue().getPackMap());
        }
        baiduDomainCMap = null;
        if (Configure.DEBUG) {
            for (Map.Entry<String, PackAbstract> map : packMap.entrySet()) {
                map.getValue().print(map.getKey());
            }
        }
    }

    private static class BaiduDomainFilterEnablerHolder {

        private static final BaiduDomainFilterEnabler INSTANCE = new BaiduDomainFilterEnabler();
    }
}
