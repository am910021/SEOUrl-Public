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
import seourl.pack.JumingPack;
import seourl.pack.SogouDomainPack;
import seourl.pack.WebArchivePack;
import seourl.template.TemplateIndex;
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

    //最終要輸出的資料
    private Map<String, WebArchivePack> wapMap = new HashMap<>();
    private Map<String, JumingPack> jpMap = new HashMap<>();
    private Map<String, SogouDomainPack> sdpMap = new HashMap<>();

    //執行中的暫存資料
    private Map<Integer, List<String>> urlSplit = new HashMap<>();
    private Map<Integer, JumingController> jcMap = new HashMap<>();
    private Map<Integer, SogouDomainController> sdcMap = new HashMap<>();

    public static void main(String[] args) {
        // TODO code application logic here

//        try {
//            System.setErr(new PrintStream(new FileOutputStream("error.log", true)));
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        }
        SEOUrl s = new SEOUrl();
        s.loadUrl();
        s.splitUrl(MAX_THREAD);
        s.startSDF(false);

        //System.out.println(Configure.DOMAIN_FILTER_MODE);
        //Configure.saveConfig();
        //SEOUrl s = new SEOUrl();
        //s.start();
    }

    /**
     * Sogou域名過濾 <br>
     * 必需執行：1.loadUrl 2.splitUrl <br>
     * 單獨呼叫時為"測式用"
     *
     * @param show 是否印出除錯訊息
     */
    private void startSDF(boolean show) {
        System.out.println("aaa");
        SogouDomainController sdc;
        for (Entry<Integer, List<String>> map : urlSplit.entrySet()) {
            System.out.println(map.getKey());
            sdc = new SogouDomainController(map.setValue(urls));
            sdcMap.put(map.getKey(), sdc);
            sdc.start();
            Tools.sleep(100, 300);
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
            for (Entry<String, SogouDomainPack> item : sdpMap.entrySet()) {
                item.getValue().print();
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
    private void startJPF(boolean show) {
        JumingController tc;
        for (Entry<Integer, List<String>> map : urlSplit.entrySet()) {
            tc = new JumingController(map.setValue(urls));
            jcMap.put(map.getKey(), tc);
            tc.start();
            Tools.sleep(100, 300);
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
                map.getValue().print();
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
        for (String u : urls) {
            WebArchiveFilter wf = new WebArchiveFilter(u);
            wf.doStart();
            wf.getWap().saveFile(u, startTime);
            wapMap.put(u, wf.getWap());
            if(show)
                wf.getWap().print();
        }
    }

    public void start() {
        this.splitUrl(MAX_THREAD);
        this.loadUrl();
        this.startWAF(false);
        this.startJPF(false);
        this.startSDF(false);

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
        String tmp;
        for (String url : urls) {
            jp = this.jpMap.get(url);
            wap = this.wapMap.get(url);

            if (wap.getTotalSize() == 0 || jp.isError() || !jp.isPass()) {
                tmp = jp.getStatus();
                failT.insertRecord(String.format("files/%s.html", url), url, wap, tmp);
                failCount++;
            } else if (wap.getTotalSize() > 0 && !jp.isError() && jp.isPass()) {
                passT.insertRecord(String.format("files/%s.html", url), url, wap, "通過");
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
}
