/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl;

import seourl.thread.JumingController;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import seourl.filter.WebArchiveFilter3;
import seourl.pack.BaiduDomainPack;
import seourl.pack.BaiduSitePack;
import seourl.pack.JumingPack;
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
import seourl.thread.WebArchiveController;
import seourl.thread.WebArchiveSnapsHotsController;

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
    private static final int MAX_THREAD = 5; //多線程，聚名網 sogou 專用
    private static final String keyWordPath = "keyword/";

    private Date startTime = new Date();
    @Getter
    private List<String> urls;
    private Map<Integer, List<String>> urlSplit;

    //最終要輸出的資料
    private Map<String, WebArchivePack> wap2Map = new TreeMap<>();   //WebArchive資料集
    private Map<String, JumingPack> jpMap = new TreeMap<>();        //聚名網資料集

    private Map<String, SogouDomainPack> sogouDomainPMap = new TreeMap<>();       //搜狗域名資料集
    private Map<String, BaiduDomainPack> baiduDomainPMap = new TreeMap<>();       //百度域名資料集

    private Map<String, So360SerachPack> so360SearchPMap = new TreeMap<>();       //360搜資料集
    private Map<String, SogouSerachPack> sogoSearchPMap = new TreeMap<>();       //百度網站資料集

    private Map<String, BaiduSitePack> baiduSitePMap = new TreeMap<>();
    private Map<String, So360SitePack> so360SitePMap = new TreeMap<>();

    //執行中的暫存資料
    private Map<Integer, WebArchiveSnapsHotsController> washcMap = new HashMap<>();
    private Map<Integer, WebArchiveController> wacMap = new HashMap<>();
    private Map<Integer, JumingController> jcMap = new HashMap<>();

    private Map<Integer, SogouDomainController> sogoDomainCMap = new HashMap<>();
    private Map<Integer, BaiduDomainController> baiduDomainCMap = new HashMap<>();

    private Map<Integer, So360SearchController> so360SearchMap = new HashMap<>();
    private Map<Integer, SogouSearchController> sogouSearchCMap = new HashMap<>();

    private Map<Integer, BaiduSiteController> baiduSiteCMap = new HashMap<>();
    private Map<Integer, So360SiteController> so360SiteCMap = new HashMap<>();

    private int totalSnapsHotsSize = 0;

    public SEOUrl() {
        checkFile();
        Configure.printStatus();
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

    private void startWebArchiveFilter(boolean show) {

        WebArchiveSnapsHotsController washc;
        for (Entry<Integer, List<String>> map : urlSplit.entrySet()) {
            washc = new WebArchiveSnapsHotsController(map.getKey(), startTime, map.getValue());
            washcMap.put(map.getKey(), washc);
            washc.start();
            Tools.sleep(100, 1000);
        }
        washc = null;
        for (Entry<Integer, WebArchiveSnapsHotsController> map : washcMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.wap2Map.putAll(map.getValue().getMWAP());
        }
        washcMap = null;
        if (show) {
            for (Entry<String, WebArchivePack> map : wap2Map.entrySet()) {
                map.getValue().print(map.getKey());
            }
        }

        long total = (System.currentTimeMillis() - startTime.getTime());
        long h = TimeUnit.MILLISECONDS.toHours(total);
        long m = TimeUnit.MILLISECONDS.toMinutes(total) - (h * 60);
        long s = TimeUnit.MILLISECONDS.toSeconds(total) - ((h * 60) + m) * 60;
        System.out.printf("快照列表讀取時間:%d小時 %d分鐘 %d秒\n", h, m, s);

        if (Configure.WEBARCHIVE_MODE != 1) {
            return;
        }

        DataSet dataSet = new DataSet();
        dataSet.setLtp(getSplitSnapsHots());
        totalSnapsHotsSize = dataSet.ltp.size();
        List<String> title = Tools.loadKeyword("WEBARCHIVE-TITLE.txt");;
        List<String> content = Tools.loadKeyword("WEBARCHIVE-CONTENT.txt");
        System.out.printf("URL數量:%d  預計讀取快照量:%d\n", urls.size(), totalSnapsHotsSize);
        WebArchiveController wac;

        for (int i = 0; i < MAX_THREAD; i++) {
            wac = new WebArchiveController(i, startTime, wap2Map, dataSet, title, content);
            wacMap.put(i, wac);
            wac.start();
            Tools.sleep(100, 1000);
        }

        wac = null;

        for (Entry<Integer, WebArchiveController> map : wacMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        wacMap = null;
        if (show) {
            for (Entry<String, WebArchivePack> map : wap2Map.entrySet()) {
                map.getValue().print(map.getKey());
                map.getValue().saveFile(map.getKey(), startTime);
            }
        }
        total = (System.currentTimeMillis() - startTime.getTime());
        h = TimeUnit.MILLISECONDS.toHours(total);
        m = TimeUnit.MILLISECONDS.toMinutes(total) - (h * 60);
        s = TimeUnit.MILLISECONDS.toSeconds(total) - ((h * 60) + m) * 60;
        System.out.printf("快照讀取時間:%d小時 %d分鐘 %d秒\n", h, m, s);
    }

    private List<TPair<String, Integer, Long>> getSplitSnapsHots() {
        List<TPair<String, Integer, Long>> ltp = new ArrayList<>();
        TPair<String, Integer, Long> tp;
        for (Entry<String, WebArchivePack> map : wap2Map.entrySet()) {
            for (Entry<Integer, List<Long>> snapshot : map.getValue().getSnapshots().entrySet()) {
                for (Long l : snapshot.getValue()) {
                    tp = new TPair(map.getKey(), snapshot.getKey(), l);
                    ltp.add(tp);
                }
            }
        }
        return ltp;
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
        for (Entry<Integer, List<String>> map : urlSplit.entrySet()) {
            ssc = new So360SiteController(map.getKey(), startTime, map.getValue(), this.loadKeyword("SO360_SITE.txt"));
            so360SiteCMap.put(map.getKey(), ssc);
            ssc.start();
            Tools.sleep(100, 1000);
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
        for (Entry<Integer, List<String>> map : urlSplit.entrySet()) {
            bsc = new BaiduSiteController(map.getKey(), startTime, map.getValue(), this.loadKeyword("BAIDU_SITE.txt"));
            baiduSiteCMap.put(map.getKey(), bsc);
            bsc.start();
            Tools.sleep(100, 1000);
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
        for (Entry<Integer, List<String>> map : urlSplit.entrySet()) {
            ssc = new SogouSearchController(map.getKey(), startTime, map.getValue(), this.loadKeyword("SOGOU_SEARCH.txt"));
            sogouSearchCMap.put(map.getKey(), ssc);
            ssc.start();
            Tools.sleep(100, 1000);
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
        for (Entry<Integer, List<String>> map : urlSplit.entrySet()) {
            bdc = new BaiduDomainController(map.getKey(), startTime, map.getValue(), this.loadKeyword("BAIDU_DOMAIN.txt"));
            baiduDomainCMap.put(map.getKey(), bdc);
            bdc.start();
            Tools.sleep(100, 1000);
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
        for (Entry<Integer, List<String>> map : urlSplit.entrySet()) {
            ssc = new So360SearchController(map.getKey(), startTime, map.getValue(), this.loadKeyword("SO360_SEARCH.txt"));
            so360SearchMap.put(map.getKey(), ssc);
            ssc.start();
            Tools.sleep(100, 1000);
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
        for (Entry<Integer, List<String>> map : urlSplit.entrySet()) {
            sdc = new SogouDomainController(map.getKey(), startTime, map.getValue(), this.loadKeyword("SOGOU_DOMAIN.txt"));
            sogoDomainCMap.put(map.getKey(), sdc);
            sdc.start();
            Tools.sleep(1000, 3000);
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
        JumingController tc;
        for (Entry<Integer, List<String>> map : urlSplit.entrySet()) {
            tc = new JumingController(map.getKey(), map.getValue());
            jcMap.put(map.getKey(), tc);
            tc.start();
            Tools.sleep(300, 1000);
        }
        tc = null;
        for (Entry<Integer, JumingController> map : jcMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.jpMap.putAll(map.getValue().getMJP());
        }
        jcMap = null;
        if (show) {
            for (Entry<String, JumingPack> map : jpMap.entrySet()) {
                map.getValue().print(map.getKey());
            }
        }
    }

    public void start() {
        if (Configure.ENABLE_WEBARCHIVE) {
            this.checkFile("WEBARCHIVE-TITLE.txt");
            this.checkFile("WEBARCHIVE-CONTENT.txt");
        }
        if (Configure.ENABLE_BAIDU_DOMAIN) {
            this.checkFile("BAIDU_DOMAIN.txt");
        }
        if (Configure.ENABLE_BAIDU_SITE) {
            this.checkFile("BAIDU_SITE.txt");
        }
        if (Configure.ENABLE_SO360_SEARCH) {
            this.checkFile("SO360_SEARCH.txt");
        }
        if (Configure.ENABLE_SO360_SITE) {
            this.checkFile("SO360_SITE.txt");
        }
        if (Configure.ENABLE_SOGOU_SEARCH) {
            this.checkFile("SOGOU_SEARCH.txt");
        }
        if (Configure.ENABLE_SOGOU_DOMAIN) {
            this.checkFile("SOGOU_DOMAIN.txt");
        }

        this.loadUrl();
        this.splitUrl(MAX_THREAD);

        if (Configure.ENABLE_WEBARCHIVE) {
            this.startWebArchiveFilter(Configure.DEBUG);
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
        saveFile();
    }

    private void splitUrl(int max) {
        urlSplit = new HashMap<>();
        int count = 0;
        for (int i = 0; i < urls.size(); i++) {
            if (!urlSplit.containsKey(count)) {
                urlSplit.put(count, (new ArrayList<>()));
            }
            urlSplit.get(count).add(urls.get(i));
            count++;
            if (count >= max) {
                count = 0;
            }
        }
    }

    private void saveFile() {

        int[] count = {0, 0, 0};

        TemplateIndex passT = new TemplateIndex(startTime);
        passT.insertTime(startTime);
        passT.setSaveName("index");

        TemplateIndex failT = new TemplateIndex(startTime);
        failT.insertTime(startTime);
        failT.setSaveName("index_f");

        for (String url : urls) {
            this.insertRecord(passT, failT, url, count);
        }
        passT.creatFile();
        failT.creatFile();
        System.out.printf("URL數量:%d  通過:%d 未通過:%d  unknow:%d  總快照量:%d\n", urls.size(), count[0], count[1], count[2], totalSnapsHotsSize);
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

            WebArchivePack wap2 = this.wap2Map.get(url);
            isPass = isPass && wap2.allPass();
            wapStr[0] = wap2.getSaveLocation();
            wapStr[1] = wap2.allPass() ? "通過" : "未通過 " + wap2.getReason();

        }

        if (Configure.ENABLE_JUMING_FILTER) {
            JumingPack jp = this.jpMap.get(url);
            isPass = isPass && jp.allPass();
            jgp = String.format("<a href=\"http://www.juming.com/hao/?cha_ym=%s\" target=\"_blank\">%s</a>", url, jp.getStatus());
        }

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
            System.out.println("aaaaaaaa");
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

    private void loadUrl() {
        urls = new ArrayList<String>();
        try {
            File file = new File("input.txt");
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String st;
            while ((st = br.readLine()) != null) {
                if (st.equals("") || st.contains("###")) {
                    continue;
                }

                urls.add(st);
            }
        } catch (IOException ex) {
            //Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<String> loadKeyword(String fileName) {
        List<String> keywords = new ArrayList<String>();
        try {
            File file = new File(keyWordPath + fileName);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String st;
            while ((st = br.readLine()) != null) {
                st.replace(" ", "").replace("\n", "");

                if (st.equals("") || st.contains("###")) {
                    continue;
                }

                keywords.add(st.toUpperCase());
            }
        } catch (IOException ex) {
            //Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return keywords;
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

    public void checkFile(String file) {
        Tools.checkDir(keyWordPath);
        File k = new File(keyWordPath + file);

        if (!k.exists()) {
            System.out.println("未找到現有" + keyWordPath + file + "檔案，已建立" + keyWordPath + file);
            List<String> comm = new ArrayList<>();
            comm.add("###請在下方加入關鍵詞，請誤刪除這行###");

            try {
                Files.write(Paths.get(keyWordPath + file), comm, StandardCharsets.UTF_8);
            } catch (IOException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (!k.exists()) {
            System.out.println("請設定好keyword資料夾設定好關鍵詞在執行。");
            System.exit(1);
        }

    }
}
