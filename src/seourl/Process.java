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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Yuri
 */
public class Process {

    //</body></html>
    String html1 = "<!DOCTYPE html><html lang=\"zh-Hant-TW\"><head><title>域名例表 - %s</title><meta charset=\"UTF-8\"><style>a:link{color: #0000FF;}a:visited{color: #FF0000;}</style></head><body><h1>域名例表 : %s</h1>";
    String html2 = "<p>輸入時間 : %s </p>";
    String html3 = "</body></html>";
    String tLink = "<a href=\"http://web.archive.org/web/%d/http://%s/\" target=\"_blank\">%d</a><br>";

    String html4 = "<!DOCTYPE html><html lang=\"zh-Hant-TW\"><head><title>域名例表 - %s </title><meta charset=\"UTF-8\"><style>a:link{color: #0000FF;}a:visited{color: #FF0000;}</style></head><body><h1>域名例表 : %s</h1>";
    String html5 = "<p>輸出時間:%s</p>";
    String html6 = "</body></html>";
    String tLink2 = "<a href=\"%s\"  target=\"_blank\">%s</a></br>";

    Map<Integer, List<Long>> snapshots = new TreeMap<>();
    private String url;
    Date startTime;

    public Process(String url, Date startTime) {
        this.startTime = startTime;
        this.url = url;
    }

    private boolean getYears() {
        try {
            String s = getJSON(String.format("https://web.archive.org/__wb/sparkline?url=%s&collection=web&output=json", url), 9000);
            JSONObject jsonO = new JSONObject(s);
            if (!jsonO.has("years")) {
                return false;
            }
            JSONObject jsonYears = jsonO.getJSONObject("years");
            Iterator<String> keys = jsonYears.keys();
            while (keys.hasNext()) {
                snapshots.put(Integer.parseInt(keys.next()), new ArrayList<Long>());
            }
            Thread.sleep(500);
        } catch (Exception ex) {
            Logger.getLogger(Process.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            return false;
        }
        
        return true;
    }

    public void start() {

        if (!getYears()) {
            return;
        }

        for (Entry<Integer, List<Long>> item : snapshots.entrySet()) {
            getSnapshotsPerYear(item.getKey());
        }
        try {
            writeToFile();
        } catch (IOException ex) {
            Logger.getLogger(Process.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getSnapshotsPerYear(int year) {
        System.out.printf("取讀 %s %d 年資料．．．．．", url, year);
        List<Long> tmp = snapshots.get(year);

        try {
            String s = getJSON(String.format("http://web.archive.org/__wb/calendarcaptures?url=%s&selected_year=%d", url, year), 9000);
            JSONArray jsonarray = new JSONArray(s);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONArray secArray = jsonarray.getJSONArray(i);
                for (int j = 0; j < secArray.length(); j++) {
                    doAnalysis(secArray.getJSONArray(j), tmp);
                }
            }
            Thread.sleep(100);
        } catch (Exception e) {
            System.out.print("失敗\n");
            // TODO Auto-generated catch block
            //e.printStackTrace();

        }
        System.out.print("完成。\n");
    }

    private void doAnalysis(JSONArray jsonA, List<Long> tmp) {
        try {
            for (int i = 0; i < jsonA.length(); i++) {
                if (jsonA.isNull(i)) {
                    continue;
                }
                JSONObject jsonO = jsonA.getJSONObject(i);
                if (jsonO.has("ts")) {
                    JSONArray ts = jsonO.getJSONArray("ts");
                    for (int j = 0; j < ts.length(); j++) {
                        long snapshot = ts.getLong(j);
                        tmp.add(snapshot);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeToFile() throws IOException {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        SimpleDateFormat sdFormat2 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date current = new Date();

        String path0 = "output/" + sdFormat2.format(startTime) + "/files/";
        String path1 = path0 + url + "-list.html";
        checkDir(path0);

        System.out.println(path1);
        
        
        
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path1),"UTF-8"));
        writer.append(String.format(html1, url, url));
        writer.append(String.format(html2, sdFormat.format(current)));
        for (Entry<Integer, List<Long>> item : snapshots.entrySet()) {
            if(item.getValue().size() <= 0)
                continue;
            
            for (Long snapshot : item.getValue()) {
                writer.append(String.format(tLink, snapshot, url, snapshot));
            }
        }
        writer.append(html3);
        writer.close();
    }

    private void writeToFile2() throws IOException {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        SimpleDateFormat sdFormat2 = new SimpleDateFormat("yyyy-MM-dd");

        String path0 = "output/" + sdFormat2.format(startTime) + "/";
        String path1 = path0 + "files/" + url + "/";
        checkDir(path1);

        BufferedWriter writer0 = new BufferedWriter(new FileWriter(path0 + "files/" + url + "-list.html", false));
        writer0.append(String.format(html4, url, url));
        writer0.append(String.format(html5, sdFormat2.format(startTime)));

        for (Entry<Integer, List<Long>> item : snapshots.entrySet()) {
            String path3 = path1 + item.getKey().toString() + ".html";

            writer0.append(String.format(tLink2, url + "/" + item.getKey() + ".html", item.getKey().toString()));

            Date current = new Date();
            BufferedWriter writer = new BufferedWriter(new FileWriter(path3, false));
            writer.append(String.format(html1, url, item.getKey(), url, item.getKey()));
            writer.append(String.format(html2, sdFormat.format(current)));
            for (Long snapshot : item.getValue()) {
                writer.append(String.format(tLink, snapshot, url, snapshot));
            }
            writer.append(html3);
            writer.close();
        }

        writer0.append(html6);
        writer0.close();

    }

    private boolean checkDir(String path) {
        try {
            File f = new File(path);
            if (f.exists()) {
                return true;
            }
            f.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static String getJSON(String url, int timeout) throws IOException {

        URL u = new URL(url);
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.setRequestMethod("GET");
        c.setUseCaches(false);
        c.setAllowUserInteraction(false);
        c.setConnectTimeout(timeout);
        c.setReadTimeout(timeout);
        c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36");
        c.connect();
        int status = c.getResponseCode();

        switch (status) {
            case 200:
            case 201:
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                return sb.toString();
        }

        return null;
    }
}
