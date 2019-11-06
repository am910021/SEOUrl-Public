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
import seourl.other.Configure;
import seourl.SEOUrl;
import seourl.data.UrlDataSet;
import seourl.other.Tools;
import seourl.enabler.ex.EnablerAbstract;
import seourl.pack.ex.PackAbstract;
import seourl.thread.JumingController;

/**
 *
 * @author Yuri
 */
public class JumingFilterEnabler extends EnablerAbstract {

    private Map<Integer, JumingController> jcMap = new HashMap<>();

    private JumingFilterEnabler() {
    }

    public static JumingFilterEnabler getInstance() {
        return JumingFilterEnablerHolder.INSTANCE;
    }

    @Override
    public void run() {
        JumingController tc;
        int maxThread = Math.min(Configure.MAX_THREAD, dsa.getSize());
        for (int i = 0; i < maxThread; i++) {
            tc = new JumingController(i, (UrlDataSet) dsa);
            jcMap.put(i, tc);
            tc.start();
            Tools.sleep(1 * 1000, 5 * 1000);
        }
        tc = null;
        for (Map.Entry<Integer, JumingController> map : jcMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.packMap.putAll(map.getValue().getMJP());
        }
        jcMap = null;
        if (Configure.DEBUG) {
            for (Map.Entry<String, PackAbstract> map : packMap.entrySet()) {
                map.getValue().print(map.getKey());
            }
        }
    }

    private static class JumingFilterEnablerHolder {

        private static final JumingFilterEnabler INSTANCE = new JumingFilterEnabler();
    }
}
