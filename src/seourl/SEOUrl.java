/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl;

import seourl.other.Configure;
import seourl.other.Tools;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import seourl.data.UrlDataSet;
import seourl.enabler.JumingFilterEnabler;
import seourl.enabler.WebArchiveFilterEnabler;
import seourl.enabler.ex.EnablerAbstract;
import seourl.pack.BaiduDomainPack;
import seourl.pack.BaiduSitePack;
import seourl.pack.So360SerachPack;
import seourl.pack.So360SitePack;
import seourl.pack.SogouDomainPack;
import seourl.pack.SogouSerachPack;
import seourl.pack.WebArchivePack;
import seourl.template.TemplateIndex;
import seourl.thread.BaiduDomainController;
import seourl.thread.BaiduSiteController;
import seourl.thread.So360SearchController;
import seourl.thread.So360SiteController;
import seourl.thread.SogouDomainController;
import seourl.thread.SogouSearchController;

/**
 *
 * @author Yuri
 */
public class SEOUrl {

    @Getter
    private int test;
    /**
     * @param args the command line arguments
     */
    private static final Logger LOG = Logger.getLogger(SEOUrl.class.getName());

    private final List<EnablerAbstract> enablerAbstractList = new ArrayList<EnablerAbstract>();

    /**
     * Get the value of enablerAbstractList
     *
     * @return the value of enablerAbstractList
     */
    private final Date startTime = new Date();
    private final UrlDataSet urlDataSet = new UrlDataSet();

    //最終要輸出的資料
    private Map<String, SogouDomainPack> sogouDomainPMap = new TreeMap<>();       //搜狗域名資料集
    private Map<String, BaiduDomainPack> baiduDomainPMap = new TreeMap<>();       //百度域名資料集

    private Map<String, So360SerachPack> so360SearchPMap = new TreeMap<>();       //360搜資料集
    private Map<String, SogouSerachPack> sogoSearchPMap = new TreeMap<>();       //百度網站資料集

    private Map<String, BaiduSitePack> baiduSitePMap = new TreeMap<>();
    private Map<String, So360SitePack> so360SitePMap = new TreeMap<>();

    //執行中的暫存資料
    private Map<Integer, SogouDomainController> sogoDomainCMap = new HashMap<>();
    private Map<Integer, BaiduDomainController> baiduDomainCMap = new HashMap<>();

    private Map<Integer, So360SearchController> so360SearchMap = new HashMap<>();
    private Map<Integer, SogouSearchController> sogouSearchCMap = new HashMap<>();

    private Map<Integer, BaiduSiteController> baiduSiteCMap = new HashMap<>();
    private Map<Integer, So360SiteController> so360SiteCMap = new HashMap<>();

    private int totalSnapsHotsSize = 0;

    public SEOUrl() {
        Configure.printStatus();
        checkFile();
        this.urlDataSet.setData(Tools.loadUrl());
    }

    public static void main(String[] args) {
        // TODO code application logic here

//        try {
//            System.setErr(new PrintStream(new FileOutputStream("error.log", true)));
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        }
        SEOUrl s = new SEOUrl();

//        List<String> title = Tools.loadKeyword("WEBARCHIVE-TITLE.txt");;
//        List<String> content = Tools.loadKeyword("WEBARCHIVE-CONTENT.txt");
//        WebArchiveFilter3 waf3 = new WebArchiveFilter3(0, title, content);
//        waf3.doAnalysis("1banchina.com", 20180805032156L);
        s.start();
        //s.waitTime();

    }

    private void waitTime() {
        while (true) {
            Tools.sleep(100);
        }
    }

    /**
     * 百度網站過濾 <br>
     * 必需執行：1.loadUrl 2.splitUrl 3.loadKeyword<br>
     * 單獨呼叫時為"測式用"
     *
     * @param show 是否印出除錯訊息
     */
    private void startSo360SiteFilter(boolean show) {
        So360SiteController ssc;
        UrlDataSet tmp = urlDataSet.getClone();
        int maxThread = Math.min(Configure.MAX_THREAD, tmp.getSize());
        for (int i = 0; i < maxThread; i++) {
            ssc = new So360SiteController(i, startTime, tmp, Tools.loadKeyword("SO360_SITE.txt"));
            so360SiteCMap.put(i, ssc);
            ssc.start();
            Tools.sleep(1 * 1000, 5 * 1000);
        }

        ssc = null;
        for (Entry<Integer, So360SiteController> map : so360SiteCMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.so360SitePMap.putAll(map.getValue().getMSDP());
        }
        so360SiteCMap = null;
        if (show) {
            for (Entry<String, So360SitePack> map : so360SitePMap.entrySet()) {
                map.getValue().print(map.getKey());
            }
        }
    }

    /**
     * 百度網站過濾 <br>
     * 必需執行：1.loadUrl 2.splitUrl 3.loadKeyword<br>
     * 單獨呼叫時為"測式用"
     *
     * @param show 是否印出除錯訊息
     */
    private void startBaiduSiteFilter(boolean show) {
        BaiduSiteController bsc;
        UrlDataSet tmp = urlDataSet.getClone();
        int maxThread = Math.min(Configure.MAX_THREAD, tmp.getSize());
        for (int i = 0; i < maxThread; i++) {
            bsc = new BaiduSiteController(i, startTime, tmp, Tools.loadKeyword("BAIDU_SITE.txt"));
            baiduSiteCMap.put(i, bsc);
            bsc.start();
            Tools.sleep(1 * 1000, 5 * 1000);
        }
        bsc = null;
        for (Entry<Integer, BaiduSiteController> map : baiduSiteCMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.baiduSitePMap.putAll(map.getValue().getMSDP());
        }
        baiduSiteCMap = null;
        if (show) {
            for (Entry<String, BaiduSitePack> map : baiduSitePMap.entrySet()) {
                map.getValue().print(map.getKey());
            }
        }
    }

    /**
     * 搜狗搜尋過濾 <br>
     * 必需執行：1.loadUrl 2.splitUrl 3.loadKeyword<br>
     * 單獨呼叫時為"測式用"
     *
     * @param show 是否印出除錯訊息
     */
    private void startSogouSearcFilter(boolean show) {
        SogouSearchController ssc;
        UrlDataSet tmp = urlDataSet.getClone();
        int maxThread = Math.min(Configure.MAX_THREAD, tmp.getSize());
        for (int i = 0; i < maxThread; i++) {
            ssc = new SogouSearchController(i, startTime, tmp, Tools.loadKeyword("SOGOU_SEARCH.txt"));
            sogouSearchCMap.put(i, ssc);
            ssc.start();
            Tools.sleep(1 * 1000, 5 * 1000);
        }
        ssc = null;
        for (Entry<Integer, SogouSearchController> map : sogouSearchCMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.sogoSearchPMap.putAll(map.getValue().getMSDP());
        }
        sogouSearchCMap = null;
        if (show) {
            for (Entry<String, SogouSerachPack> map : sogoSearchPMap.entrySet()) {
                map.getValue().print(map.getKey());
            }
        }
    }

    /**
     * 百度域名過濾 <br>
     * 必需執行：1.loadUrl 2.splitUrl 3.loadKeyword<br>
     * 單獨呼叫時為"測式用"
     *
     * @param show 是否印出除錯訊息
     */
    private void startBaiduDomainFilter(boolean show) {
        BaiduDomainController bdc;
        UrlDataSet tmp = urlDataSet.getClone();
        int maxThread = Math.min(Configure.MAX_THREAD, tmp.getSize());
        for (int i = 0; i < maxThread; i++) {
            bdc = new BaiduDomainController(i, startTime, tmp, Tools.loadKeyword("BAIDU_DOMAIN.txt"));
            baiduDomainCMap.put(i, bdc);
            bdc.start();
            Tools.sleep(1 * 1000, 5 * 1000);
        }
        bdc = null;
        for (Entry<Integer, BaiduDomainController> map : baiduDomainCMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.baiduDomainPMap.putAll(map.getValue().getMDP());
        }
        baiduDomainCMap = null;
        if (show) {
            for (Entry<String, BaiduDomainPack> map : baiduDomainPMap.entrySet()) {
                map.getValue().print(map.getKey());
            }
        }
    }

    /**
     * 360搜過濾 <br>
     * 必需執行：1.loadUrl 2.splitUrl 3.loadKeyword<br>
     * 單獨呼叫時為"測式用"
     *
     * @param show 是否印出除錯訊息
     */
    private void startSo360SearchFIlter(boolean show) {
        So360SearchController ssc;
        UrlDataSet tmp = urlDataSet.getClone();
        int maxThread = Math.min(Configure.MAX_THREAD, tmp.getSize());
        for (int i = 0; i < maxThread; i++) {
            ssc = new So360SearchController(i, startTime, tmp, Tools.loadKeyword("SO360_SEARCH.txt"));
            so360SearchMap.put(i, ssc);
            ssc.start();
            Tools.sleep(1 * 1000, 5 * 1000);
        }
        ssc = null;
        for (Entry<Integer, So360SearchController> map : so360SearchMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.so360SearchPMap.putAll(map.getValue().getMSDP());
        }
        so360SearchMap = null;
        if (show) {
            for (Entry<String, So360SerachPack> map : so360SearchPMap.entrySet()) {
                map.getValue().print(map.getKey());
            }
        }
    }

    /**
     * 搜狗域名過濾 <br>
     * 必需執行：1.loadUrl 2.splitUrl 3.loadKeyword<br>
     * 單獨呼叫時為"測式用"
     *
     * @param show 是否印出除錯訊息
     */
    private void startSogouDomainFilter(boolean show) {
        SogouDomainController sdc;
        UrlDataSet tmp = urlDataSet.getClone();
        int maxThread = Math.min(Configure.MAX_THREAD, tmp.getSize());
        for (int i = 0; i < maxThread; i++) {
            sdc = new SogouDomainController(i, startTime, tmp, Tools.loadKeyword("SOGOU_DOMAIN.txt"));
            sogoDomainCMap.put(i, sdc);
            sdc.start();
            Tools.sleep(1 * 1000, 5 * 1000);
        }
        sdc = null;

        for (Entry<Integer, SogouDomainController> map : sogoDomainCMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.sogouDomainPMap.putAll(map.getValue().getMSDP());
        }
        sogoDomainCMap = null;
        if (show) {
            for (Entry<String, SogouDomainPack> map : sogouDomainPMap.entrySet()) {
                map.getValue().print(map.getKey());
            }
        }
    }

    /**
     * 聚名網過濾 <br>
     * 必需執行：1.loadUrl 2.splitUrl <br>
     * 單獨呼叫時為"測式用"
     *
     * @param show 是否印出除錯訊息
     */
    private void startJF(boolean show) {

    }

    public void start() {
        if (Configure.ENABLE_WEBARCHIVE) {
            enablerAbstractList.add(WebArchiveFilterEnabler.getInstance());
            WebArchiveFilterEnabler.getInstance().setDsa(urlDataSet.getClone());
        }

        if (Configure.ENABLE_JUMING_FILTER) {
            enablerAbstractList.add(JumingFilterEnabler.getInstance());
            JumingFilterEnabler.getInstance().setDsa(urlDataSet.getClone());
        }

        if (Configure.ENABLE_BAIDU_DOMAIN) {
            Tools.checkKeyWordFile("BAIDU_DOMAIN.txt");
        }
        if (Configure.ENABLE_BAIDU_SITE) {
            Tools.checkKeyWordFile("BAIDU_SITE.txt");
        }
        if (Configure.ENABLE_SO360_SEARCH) {
            Tools.checkKeyWordFile("SO360_SEARCH.txt");
        }
        if (Configure.ENABLE_SO360_SITE) {
            Tools.checkKeyWordFile("SO360_SITE.txt");
        }
        if (Configure.ENABLE_SOGOU_SEARCH) {
            Tools.checkKeyWordFile("SOGOU_SEARCH.txt");
        }
        if (Configure.ENABLE_SOGOU_DOMAIN) {
            Tools.checkKeyWordFile("SOGOU_DOMAIN.txt");
        }

        if (Configure.ENABLE_JUMING_FILTER) {
            this.startJF(Configure.DEBUG);
        }
        if (Configure.ENABLE_BAIDU_DOMAIN) {
            this.startBaiduDomainFilter(Configure.DEBUG);
        }
        if (Configure.ENABLE_BAIDU_SITE) {
            this.startBaiduSiteFilter(Configure.DEBUG);
        }
        if (Configure.ENABLE_SO360_SEARCH) {
            this.startSo360SearchFIlter(Configure.DEBUG);
        }
        if (Configure.ENABLE_SO360_SITE) {
            this.startSo360SiteFilter(Configure.DEBUG);
        }
        if (Configure.ENABLE_SOGOU_SEARCH) {
            this.startSogouSearcFilter(Configure.DEBUG);
        }
        if (Configure.ENABLE_SOGOU_DOMAIN) {
            this.startSogouDomainFilter(Configure.DEBUG);
        }
        //Tools.sleep(100000);
        for (EnablerAbstract ea : enablerAbstractList) {
            ea.start();
        }
        try {
            WebArchiveFilterEnabler.getInstance().join();
        } catch (InterruptedException ex) {
            Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (Configure.ENABLE_WEBARCHIVE) {
            totalSnapsHotsSize = WebArchiveFilterEnabler.getInstance().getDsa().getSize();
            System.out.printf("URL數量:%d  總快照量:%d\r\n", urlDataSet.getSize(), totalSnapsHotsSize);
        }

        saveFile();
    }

    private void saveFile() {

        int[] count = {0, 0, 0};

        TemplateIndex passT = new TemplateIndex(startTime);
        passT.insertTime(startTime);
        passT.setSaveName("index");

        TemplateIndex failT = new TemplateIndex(startTime);
        failT.insertTime(startTime);
        failT.setSaveName("index_f");

        for (String url : urlDataSet.getListCopy()) {
            this.insertRecord(passT, failT, url, count);
        }
        passT.creatFile();
        failT.creatFile();
        System.out.printf("URL數量:%d  通過:%d 未通過:%d  unknow:%d  \r\n", urlDataSet.getSize(), count[0], count[1], count[2]);
        long total = (System.currentTimeMillis() - startTime.getTime());
        long h = TimeUnit.MILLISECONDS.toHours(total);
        long m = TimeUnit.MILLISECONDS.toMinutes(total) - (h * 60);
        long s = TimeUnit.MILLISECONDS.toSeconds(total) - ((h * 60) + m) * 60;
        System.out.printf("總執行時間:%d小時 %d分鐘 %d秒\n", h, m, s);
    }

    private void insertRecord(TemplateIndex passT, TemplateIndex failT, String url, int[] count) {

        String[] wapStr = {"", "未啟用"};
        String jgp = "未啟用";
        String[] bdpStr = {"", "未啟用"};
        String[] bspStr = {"", "未啟用"};
        String[] s3sepStr = {"", "未啟用"};
        String[] s3sipStr = {"", "未啟用"};
        String[] sdpStr = {"", "未啟用"};
        String[] sspStr = {"", "未啟用"};

        boolean isPass = true;

        if (Configure.ENABLE_WEBARCHIVE) {

            WebArchivePack wap2 = WebArchiveFilterEnabler.getInstance().getWap2Map().get(url);
            isPass = isPass && wap2.allPass();
            wapStr[0] = wap2.getSaveLocation();
            wapStr[1] = wap2.allPass() ? "通過" : "未通過 " + wap2.getReason();

        }
//        if (Configure.ENABLE_JUMING_FILTER) {
//            JumingPack jp = this.jpMap.get(url);
//            isPass = isPass && jp.allPass();
//            jgp = String.format("<a href=\"http://www.juming.com/hao/?cha_ym=%s\" target=\"_blank\">%s</a>", url, jp.getStatus());
//        }
        if (Configure.ENABLE_BAIDU_DOMAIN) {
            BaiduDomainPack bdp = this.baiduDomainPMap.get(url);
            isPass = isPass && bdp.allPass();
            bdpStr[0] = bdp.getSaveLocation();
            bdpStr[1] = bdp.allPass() ? "通過" : "未通過";
        }
        if (Configure.ENABLE_BAIDU_SITE) {
            BaiduSitePack bsp = this.baiduSitePMap.get(url);
            isPass = isPass && bsp.allPass();
            bspStr[0] = bsp.getSaveLocation();
            bspStr[1] = bsp.allPass() ? "通過" : "未通過";
        }
        if (Configure.ENABLE_SO360_SEARCH) {
            So360SerachPack s3sep = this.so360SearchPMap.get(url);
            isPass = isPass && s3sep.allPass();
            s3sepStr[0] = s3sep.getSaveLocation();
            s3sepStr[1] = s3sep.allPass() ? "通過" : "未通過";
        }
        if (Configure.ENABLE_SO360_SITE) {
            So360SitePack s3sip = this.so360SitePMap.get(url);
            isPass = isPass && s3sip.allPass();
            s3sipStr[0] = s3sip.getSaveLocation();
            s3sipStr[1] = s3sip.allPass() ? "通過" : "未通過";
        }
        if (Configure.ENABLE_SOGOU_DOMAIN) {
            SogouDomainPack sdp = this.sogouDomainPMap.get(url);
            isPass = isPass && sdp.allPass();
            sdpStr[0] = sdp.getSaveLocation();
            sdpStr[1] = sdp.allPass() ? "通過" : "未通過";
        }
        if (Configure.ENABLE_SOGOU_SEARCH) {
            SogouSerachPack ssp = this.sogoSearchPMap.get(url);
            isPass = isPass && ssp.allPass();
            sspStr[0] = ssp.getSaveLocation();
            sspStr[1] = ssp.allPass() ? "通過" : "未通過";
        }

//        String[] bdpStr = {"", "未啟用"};
//        String[] bspStr = {"", "未啟用"};
//        String[] s3sepStr = {"", "未啟用"};
//        String[] s3sipStr = {"", "未啟用"};
//        String[] sdpStr = {"", "未啟用"};
//        String[] sspStr = {"", "未啟用"};
        if (!isPass) {
            failT.insertRecord(url, wapStr, jgp,
                    bdpStr,
                    bspStr,
                    s3sepStr,
                    s3sipStr,
                    sdpStr,
                    sspStr);
            count[1]++;
        } else {
            passT.insertRecord(url, wapStr, jgp,
                    bdpStr,
                    bspStr,
                    s3sepStr,
                    s3sipStr,
                    sdpStr,
                    sspStr);
            count[0]++;
        }
    }

    public void checkFile() {

        File i = new File("input.txt");

        if (!i.exists()) {
            System.out.println("未找到現有input.txt檔案，已建立input.txt");
            List<String> comm = new ArrayList<>();
            comm.add("###請在下方加域名，請誤刪除這行###");
            try {
                Files.write(Paths.get("input.txt"), comm, StandardCharsets.UTF_8);
            } catch (IOException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (!i.exists()) {
            System.out.println("請設定好keywords.txt或input.txt在執行。");
            System.exit(1);
        }

    }

}
