/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
import lombok.Getter;

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
    Date startTime = new Date();
    List<String> urls = new ArrayList<String>();
    Map<String,  List<Long>> record = new TreeMap<>();

    static final String html1 = "<!DOCTYPE html><html lang=\"zh-Hant-TW\"><head><title>域名例表</title><meta charset=\"UTF-8\"><style>a:link{color: #0000FF;}a:visited{color: #FF0000;}</style></head><body><h1>域名例表</h1>";
    static final String html2 = "<p>輸出時間:%s</p>";
    static final String html3 = "</body></html>";
    static final String tLink1 = "<a href=\"%s\"  target=\"_blank\">%s</a></br>";

    public static void main(String[] args) {
        // TODO code application logic here
        Template t = new Template("index");
        t.insertByKey("time", "aaaaaaa");
        t.insertByKey("hasRecord", "aaaaaaa","bbbbbbbbb");
        t.insertByKey("nonRecord", "bbbbbbb","aaaaaaa");
        t.creatFile();
        
        //SEOUrl s = new SEOUrl();
       // s.start();
    }

    public void start() {
        loadUrl();
        for (String url : urls) {
            Process p = new Process(url, startTime);
            p.start();
        }
        try {
            writeToFile();
        } catch (Exception ex) {
            Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void writeToFile() throws Exception {
        SimpleDateFormat sdFormat2 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        String path0 = "output/" + sdFormat2.format(startTime) + "/";
        String path1 = sdFormat2.format(startTime) + "/";

        BufferedWriter writer0 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path0 + "index.html"), "UTF-8"));
        writer0.append(html1);
        writer0.append(String.format(html2, sdFormat2.format(startTime)));
        for (String url : urls) {
            writer0.append(String.format(tLink1, "files/" + url + "-list.html", url));
        }

        writer0.append(html3);
        writer0.close();

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
