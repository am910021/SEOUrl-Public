/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl;

import seourl.thread.JumingController;
import seourl.filter.WebArchiveFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import seourl.filter.BaiduDomainFilter;
import seourl.filter.So360SearchFilter;
import seourl.filter.SogouDomainFilter;
import seourl.filter.ex.DomainFilterAbstract;
import seourl.pack.JumingPack;
import seourl.pack.SearchEnginePack;
import seourl.pack.WebArchivePack;
import seourl.template.TemplateIndex;
import seourl.thread.BaiduDomainController;
import seourl.thread.So360SearchController;
import seourl.thread.SogouDomainController;

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
    private static final int MAX_THREAD = 4; //多線程，聚名網 sogou 專用

    private Date startTime = new Date();
    private List<String> urls = new ArrayList<String>();
    @Getter
    private List<String> keywords = new ArrayList<String>();

    //最終要輸出的資料
    private Map<String, WebArchivePack> wapMap = new HashMap<>();   //WebArchive資料集
    private Map<String, JumingPack> jpMap = new HashMap<>();        //聚名網資料集
    private Map<String, SearchEnginePack> sdpMap = new HashMap<>();       //搜狗域名資料集
    private Map<String, SearchEnginePack> bdpMap = new HashMap<>();       //百度域名資料集
    private Map<String, SearchEnginePack> sspMap = new HashMap<>();       //360搜資料集

    //執行中的暫存資料
    private Map<Integer, List<String>> urlSplit = new HashMap<>();
    private Map<Integer, JumingController> jcMap = new HashMap<>();
    private Map<Integer, SogouDomainController> sdcMap = new HashMap<>();
    private Map<Integer, BaiduDomainController> bdcMap = new HashMap<>();
    private Map<Integer, So360SearchController> sscMap = new HashMap<>();

    public static void main(String[] args) {
        // TODO code application logic here

//        try {
//            System.setErr(new PrintStream(new FileOutputStream("error.log", true)));
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        }
        


        SEOUrl s = new SEOUrl();
        s.loadUrl();
        s.splitUrl(4);
        s.loadKeyword();
        s.startSSF(true);
        
        
        //s.splitUrl(MAX_THREAD);
        //s.startBDF(true);
        //s.startSDF(true);
        //DomainFilterAbstract bdf = new BaiduDomainFilter();
        //bdf.doAnalysis("google.com");
        //System.out.println(Configure.DOMAIN_FILTER_MODE);
        //Configure.saveConfig();
        //s.start();
    }

    /**
     * 百度域名過濾 <br>
     * 必需執行：1.loadUrl 2.splitUrl 3.loadKeyword<br>
     * 單獨呼叫時為"測式用"
     *
     * @param show 是否印出除錯訊息
     */
    private void startBDF(boolean show) {
        BaiduDomainController bdc;
        for (Entry<Integer, List<String>> map : urlSplit.entrySet()) {
            bdc = new BaiduDomainController(map.getKey(), map.getValue(), keywords);
            bdcMap.put(map.getKey(), bdc);
            bdc.start();
            Tools.sleep(100, 1000);
        }
        bdc = null;
        for (Entry<Integer, BaiduDomainController> map : bdcMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.bdpMap.putAll(map.getValue().getMDP());
        }
        bdcMap = null;
        if (show) {
            for (Entry<String, SearchEnginePack> map : bdpMap.entrySet()) {
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
    private void startSSF(boolean show) {
        So360SearchController ssc;
        for (Entry<Integer, List<String>> map : urlSplit.entrySet()) {
            ssc = new So360SearchController(map.getKey(), map.getValue(), keywords);
            sscMap.put(map.getKey(), ssc);
            ssc.start();
            Tools.sleep(100, 1000);
        }
        ssc = null;
        for (Entry<Integer, So360SearchController> map : sscMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.sspMap.putAll(map.getValue().getMSDP());
        }
        sscMap = null;
        if (show) {
            for (Entry<String, SearchEnginePack> map : sspMap.entrySet()) {
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
    private void startSDF(boolean show) {
        SogouDomainController sdc;
        for (Entry<Integer, List<String>> map : urlSplit.entrySet()) {
            sdc = new SogouDomainController(map.getKey(), map.getValue(), keywords);
            sdcMap.put(map.getKey(), sdc);
            sdc.start();
            Tools.sleep(1000, 3000);
        }
        sdc = null;

        for (Entry<Integer, SogouDomainController> map : sdcMap.entrySet()) {
            try {
                map.getValue().join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.sdpMap.putAll(map.getValue().getMSDP());
        }
        sdcMap = null;
        if (show) {
            for (Entry<String, SearchEnginePack> map : sdpMap.entrySet()) {
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

    /**
     * WebArchive快照過濾 <br>
     * 必需執行：無 <br>
     * 單獨呼叫時為"測式用"
     *
     * @param show 是否印出除錯訊息
     */
    private void startWAF(boolean show) {
        for (String url : urls) {
            WebArchiveFilter wf = new WebArchiveFilter(url);
            wf.doStart();
            wf.getWap().saveFile(url, startTime);
            wapMap.put(url, wf.getWap());
            if (show) {
                wf.getWap().print(url);
            }
        }
    }

    public void start() {

        this.loadUrl();
        this.splitUrl(MAX_THREAD);
        this.loadKeyword();
        this.startWAF(true);
        this.startJF(true);
        this.startSDF(true);
        this.startBDF(true);

        saveFile();
    }

    private void splitUrl(int max) {
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
        int passCount = 0;
        TemplateIndex passT = new TemplateIndex(startTime);
        passT.insertTime(startTime);
        passT.setSaveName("index");

        int failCount = 0;
        TemplateIndex failT = new TemplateIndex(startTime);
        failT.insertTime(startTime);
        failT.setSaveName("index_f");

        int unknowCount = 0;
        JumingPack jp;
        WebArchivePack wap;
        SearchEnginePack sdp;
        SearchEnginePack bdp;
        for (String url : urls) {
            jp = this.jpMap.get(url);
            wap = this.wapMap.get(url);
            sdp = this.sdpMap.get(url);
            bdp = this.bdpMap.get(url);

            if (wap.getTotalSize() == 0 || jp.isError() || !jp.isPass() || sdp.isIllegal() || sdp.isError() || bdp.isIllegal() || bdp.isError()) {
                failT.insertRecord(String.format("files/%s.html", url), url, wap, jp.getStatus(), sdp.getStatus(), bdp.getStatus());
                failCount++;
            } else if (wap.getTotalSize() > 0 && !jp.isError() && jp.isPass()) {
                passT.insertRecord(String.format("files/%s.html", url), url, wap, "通過", "通過", "通過");
                passCount++;
            } else {
                unknowCount++;
            }
            //System.out.println(jp.toString());
            //this.mWebArchive.get(url).saveFile(url, startTime);
        }
        passT.creatFile();
        failT.creatFile();
        System.out.printf("total:%d  pass:%d fail:%d  unknow:%d \n", urls.size(), passCount, failCount, unknowCount);
    }

    private void loadUrl() {
        urls = new ArrayList<String>();
        try {
            File file = new File("input.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                if (st.equals("")) {
                    continue;
                }

                urls.add(st);
            }
        } catch (IOException ex) {
            //Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadKeyword() {
        keywords = new ArrayList<String>();
        try {
            File file = new File("keywords.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                st.replace(" ", "").replace("\n", "");

                if (st.equals("")) {
                    continue;
                }

                keywords.add(st);
            }
        } catch (IOException ex) {
            //Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
