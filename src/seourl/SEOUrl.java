/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl;

import seourl.filter.WebArchiveFilter;
import template.Template;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import lombok.Getter;
import seourl.filter.JumingFilter;
import seourl.filter.Link114Filter;
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
    private Date startTime = new Date();
    private List<String> urls = new ArrayList<String>();
    // url year loadTime snapshots
    private Map<String, WebArchivePack> mWebArchive = new HashMap<>();
    private Map<String, JumingPack> mJP = new HashMap<>();

    private Map<Integer, List<String>> proxy = new HashMap<>();
    private Map<Thread, ThreadController> threadMap = new HashMap<>();

    public static void main(String[] args) {
        // TODO code application logic here
        SEOUrl s = new SEOUrl();
        s.start();

    }

    public void start() {
        loadUrl();
        for (String u : urls) {
            WebArchiveFilter wf = new WebArchiveFilter(u);
            wf.doStart();
            mWebArchive.put(u, wf.getWap());
        }

        splitUrl(4);
        ThreadController tc;
        Thread t;
        for (Entry<Integer, List<String>> map : proxy.entrySet()) {
            tc = new ThreadController(map.setValue(urls));
            t = new Thread(tc);
            threadMap.put(t, tc);
            t.start();
            Tools.sleep(100,300);
        }
        tc = null;
        t=null;
        
        for(Entry<Thread, ThreadController> tm : threadMap.entrySet() ){
            try {
                tm.getKey().join();
                
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            this.mJP.putAll(tm.getValue().getMJP());
        }

        write2File();

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

    private void write2File() {
        TemplateIndex tIndex = new TemplateIndex(startTime);
        tIndex.insertTime(startTime);

        for (String url : urls) {
            tIndex.insertRecord(String.format("files/%s.html", url), url, this.mWebArchive.get(url).getTotalSize());
            this.mWebArchive.get(url).saveFile(url, startTime);
        }
        tIndex.creatFile();
    }

    void loadUrl() {
        try {
            File file = new File("input.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                urls.add(st);
            }
        } catch (IOException ex) {
            Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
