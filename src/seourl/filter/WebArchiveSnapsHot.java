/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;
import seourl.other.Tools;
import seourl.filter.ex.BasicFilterAbstract;
import seourl.pack.WebArchivePack;
import seourl.type.Filter;

/**
 *
 * @author Yuri
 */
public class WebArchiveSnapsHot extends BasicFilterAbstract {

    @Getter
    private WebArchivePack wap;

    public WebArchiveSnapsHot(int pid, Filter filter) {
        super(pid, filter);
    }

    // @Override
    protected void createNewSearchEnginePack(String url) {
        this.wap = new WebArchivePack(url);
    }

    public boolean doAnalysis(String url) {
        createNewSearchEnginePack(url);

        boolean status = !getYears(url); //讀取快照有的年份

        if (status) {
            wap.setUrlErrpr(status);
            return false;
        }

        for (Map.Entry<Integer, List<Long>> item : wap.getSnapshots().entrySet()) {
            status = !getSnapshotsPerYear(url, item.getKey());
            if (status) {
                wap.getYearError().put(item.getKey(), status);
                return false;
            }
            Collections.sort(item.getValue());
            Collections.reverse(item.getValue());
            Tools.sleep(3000, 10000);
        }
        return true;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    private int timeConvert(Long t) {
//        String tmp = t.toString();
//        int year = Integer.parseInt(tmp.substring(0, 4));
//        int month = Integer.parseInt(tmp.substring(4, 6));
//        int day = Integer.parseInt(tmp.substring(6, 8));
//        int hours = Integer.parseInt(tmp.substring(8, 10));
//        int minute = Integer.parseInt(tmp.substring(10, 12));
//        int second = Integer.parseInt(tmp.substring(12));
//        return Integer.parseInt(String.format("%d%02d", year, month));
//    }
    private boolean getYears(String url) {
        String s = "";
        boolean status = false;
        int count = 0;
        while (!status && count < 15) {
            count++;
            try {
                s = Tools.getJSON(String.format("https://web.archive.org/__wb/sparkline?url=%s", url), 15000);
                if (s.equals("")) {
                    System.out.printf("線程-%d 取讀 %s 年代參數．．．．．失敗。\r\n", pid, url);
                    Tools.sleep(10 * 1000, 20 * 1000);
                    continue;
                }

                status = true;
                System.out.printf("線程-%d 取讀 %s 年代參數．．．．．成功。\r\n", pid, url);
            } catch (Exception ex) {
                Tools.printError(filter, ex);
                System.out.printf("線程-%d 取讀 %s 年代參數．．．．．失敗。\r\n", pid, url);
                Tools.sleep(10 * 1000, 20 * 1000);
            }
        }
        if (count >= 15 && s.equals("")) {
            return false;
        }

        try {
            JSONObject jsonO = new JSONObject(s);
            if (!jsonO.has("years")) {
                return false;
            }
            JSONObject jsonYears = jsonO.getJSONObject("years");

            @SuppressWarnings("unchecked")
            Iterator<String> keys = jsonYears.keys();
            while (keys.hasNext()) {
                wap.getSnapshots().put(Integer.parseInt(keys.next()), new ArrayList<Long>());
            }
            Thread.sleep(1, 200);
        } catch (Exception ex) {
            Tools.printError(filter, ex);
            return false;
        }
        return true;
    }

    private boolean getSnapshotsPerYear(String url, int year) {
        List<Long> tmp = wap.getSnapshots().get(year);
        String s = "";
        boolean status = false;
        int count = 0;
        while (!status && count < 15) {
            count++;
            try {
                s = Tools.getJSON(String.format("http://web.archive.org/__wb/calendarcaptures?url=%s&selected_year=%d", url, year), 15000);
                if (s.equals("")) {
                    System.out.printf("線程-%d 取得 %s %d 年快照參數．．．．．失敗。\r\n", pid, url, year);
                    Tools.sleep(10 * 1000, 20 * 1000);
                    continue;
                }

                status = true;
                System.out.printf("線程-%d 取得 %s %d 年快照參數．．．．．成功。\r\n", pid, url, year);
            } catch (Exception ex) {
                Tools.printError(filter, ex);
                System.out.printf("線程-%d 取得 %s %d 年快照參數．．．．．失敗。\r\n", pid, url, year);
                Tools.sleep(10 * 1000, 20 * 1000);
            }
        }

        if (count >= 15 && s.equals("")) {
            return false;
        }

        Map<Integer, Long> map = new TreeMap<>(Collections.reverseOrder());
        try {
            JSONArray jsonarray = new JSONArray(s);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONArray secArray = jsonarray.getJSONArray(i);
                for (int j = 0; j < secArray.length(); j++) {
                    analysisJson(secArray.getJSONArray(j), tmp);
                }
            }
        } catch (Exception e) {
            Tools.printError(filter, e);
            System.out.printf("線程-%d %s %d 年快照參數無法分析。 \r\n", pid, url, year);
        }
        return true;
    }

    private void analysisJson(JSONArray jsonA, List<Long> list) {
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
                        list.add(snapshot);
                    }
                }
            }
        } catch (Exception e) {
            Tools.printError(filter, e);
        }
    }

}
