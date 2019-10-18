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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import seourl.filter.SogouDomainFilter;
import seourl.pack.JumingPack;
import seourl.pack.WebArchivePack;
import template.TemplateIndex;

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

    private Date startTime = new Date();
    private List<String> urls = new ArrayList<String>();
    // url year loadTime snapshots
    private Map<String, WebArchivePack> mWebArchive = new HashMap<>();
    private Map<String, JumingPack> mJP = new HashMap<>();

    private Map<Integer, List<String>> proxy = new HashMap<>();
    private Map<Thread, JumingController> threadMap = new HashMap<>();

    public static void main(String[] args) {
        // TODO code application logic here

//        try {
//            System.setErr(new PrintStream(new FileOutputStream("error.log", true)));
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        }

        SogouDomainFilter sdf = new SogouDomainFilter();
        sdf.doAnalysis("https://www.sogou.com/web?query=\"chinaqscm.com\"");
        
        
        //System.out.println(Configure.DOMAIN_FILTER_MODE);
        //Configure.saveConfig();
        //SEOUrl s = new SEOUrl();
        //s.start();

    }

    public void start() {
        loadUrl();
        for (String u : urls) {
            WebArchiveFilter wf = new WebArchiveFilter(u);
            wf.doStart();
            wf.getWap().saveFile(u, startTime);
            mWebArchive.put(u, wf.getWap());
        }

        splitUrl(4);
        JumingController tc;
        Thread t;
        for (Entry<Integer, List<String>> map : proxy.entrySet()) {
            tc = new JumingController(map.setValue(urls));
            t = new Thread(tc);
            threadMap.put(t, tc);
            t.start();
            Tools.sleep(100, 300);
        }
        tc = null;
        t = null;

        for (Entry<Thread, JumingController> tm : threadMap.entrySet()) {
            try {
                tm.getKey().join();

            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }

            this.mJP.putAll(tm.getValue().getMJP());
        }

        saveFile();

    }

    private void splitUrl(int max) {
        int count = 0;
        for (int i = 0; i < urls.size(); i++) {
            if (!proxy.containsKey(count)) {
                proxy.put(count, (new ArrayList<>()));
            }
            proxy.get(count).add(urls.get(i));
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
            jp = this.mJP.get(url);
            wap = this.mWebArchive.get(url);

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

    void loadUrl() {
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
