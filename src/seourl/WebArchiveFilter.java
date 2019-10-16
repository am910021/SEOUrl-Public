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
import java.net.SocketTimeoutException;
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
import javafx.util.Pair;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;
import seourl.pack.WebArchivePack;

/**
 *
 * @author Yuri
 */
public class WebArchiveFilter {

    @Getter
    WebArchivePack wap = new WebArchivePack();
    private String url;

    public WebArchiveFilter(String url) {
        this.url = url;
    }

    private boolean getYears() {
        String s = "";
        boolean status = false;
        while (!status) {
            System.out.printf("取讀 %s 年代資料．．．．．", url);
            try {
                s = getJSON(String.format("https://web.archive.org/__wb/sparkline?url=%s&collection=web&output=json", url), 9000);
                status = true;
                System.out.println("功成。");
            } catch (Exception ex) {
                //Logger.getLogger(WebArchiveFilter.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("失敗。");
                Tools.sleep(1000);
            }
        }

        try {
            JSONObject jsonO = new JSONObject(s);
            if (!jsonO.has("years")) {
                return false;
            }
            JSONObject jsonYears = jsonO.getJSONObject("years");
            Iterator<String> keys = jsonYears.keys();
            while (keys.hasNext()) {
                //
                wap.setReadTime(System.currentTimeMillis());
                wap.put(Integer.parseInt(keys.next()), new ArrayList<Long>());
            }
            Thread.sleep(500);
        } catch (Exception ex) {
            Logger.getLogger(WebArchiveFilter.class.getName()).log(Level.SEVERE, null, ex);
            //ex.printStackTrace();
            return false;
        }

        return true;
    }

    public void doStart() {
        if (!getYears()) {
            return;
        }
        for (Entry<Integer, List<Long>> item : wap.getSnapshots().entrySet()) {
            getSnapshotsPerYear(item.getKey());
            Tools.sleep(100);
        }
    }

    private void getSnapshotsPerYear(int year) {
        List<Long> tmp = wap.getSnapshots().get(year);
        String s = "";
        boolean status = false;
        while (!status) {
            System.out.printf("取讀 %s %d 年快照資料．．．．．", url, year);
            try {
                s = getJSON(String.format("http://web.archive.org/__wb/calendarcaptures?url=%s&selected_year=%d", url, year), 9000);
                status = true;
                System.out.println("功成。");
            } catch (Exception ex) {
                Logger.getLogger(WebArchiveFilter.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("失敗。");
                Tools.sleep(1000);
            }
        }

        try {
            JSONArray jsonarray = new JSONArray(s);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONArray secArray = jsonarray.getJSONArray(i);
                for (int j = 0; j < secArray.length(); j++) {
                    doAnalysis(secArray.getJSONArray(j), tmp);
                }
            }
        } catch (Exception e) {
            System.out.printf("%s %d 年快照資料無法分析。 \n", url, year);
        }
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
        wap.addTotalSize(tmp.size());
    }

    private static String getJSON(String url, int timeout) throws Exception {

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
