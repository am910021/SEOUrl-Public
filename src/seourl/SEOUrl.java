/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl;

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
import lombok.Getter;
import seourl.pack.WebArchivePack;

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
        write2File();
    }

    private void write2File() {
        Template tIndex = new Template("index", startTime);
        tIndex.insertByKey("time", Tools.getFormatDate1(startTime));
        Template tWebArch;
        for (String url : urls) {
            int size = this.mWebArchive.get(url).getTotalSize();
            if (size == 0) {
                tIndex.insertByKey("nonRecord", url);
            } else {
                tIndex.insertByKey("hasRecord", String.format("files/%s.html", url), url);
                tWebArch = new Template("webarchive", startTime);
                tWebArch.setSavePath("files");
                tWebArch.setSaveName(url);
                tWebArch.insertByKey("title", url);
                tWebArch.insertByKey("domainName", url);
                tWebArch.insertByKey("time", Tools.getFormatDate1(this.mWebArchive.get(url).getReadTime()));
                for (Entry<Integer, List<Long>> entry : this.mWebArchive.get(url).getSnapshots().entrySet()) {
                    for (long snapshot : entry.getValue()) {
                        tWebArch.insertByKey("record", snapshot, url, snapshot);
                    }
                }
                tWebArch.creatFile();
            }
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
