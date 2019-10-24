/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.html.DomElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import seourl.Configure;
import seourl.Tools;
import seourl.filter.ex.FilterAbstract;
import seourl.pack.WebArchivePack2;

/**
 *
 * @author Yuri
 */
public class WebArchiveFilter2 extends FilterAbstract {

    @Getter
    private WebArchivePack2 wap;

    private List<String> listTitle;
    private List<String> listContent;

    public WebArchiveFilter2(List<String> listTitle, List<String> listContent) {
        super();
        this.listTitle = listTitle;
        this.listContent = listContent;
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setTimeout(10000);
    }

    // @Override
    protected void createNewSearchEnginePack() {
        this.wap = new WebArchivePack2();
    }

    public boolean getPage(String url, long s) {
        boolean status = false;
        int count = 0;
        while ((!status && count < Configure.WEBARCH_TRY_REDECT_TIMES)) {
            try {
                page = webClient.getPage(String.format(Configure.WEBARCHIVE, s, url));
                if (page.asText().contains("Redirecting")) {
                    List<DomElement> l = page.getByXPath("//p[@class='impatient']/a");
                    if (l.size() > 0) {
                        page = l.get(0).click();
                    }
                }
                status = true;
                System.out.printf("取得 %s %d 快照成功。", url, s);
            } catch (Exception ex) {
                //Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                System.out.printf("取得 %s %d 快照失敗。", url, s);
                Tools.sleep(20, 200);
            }
            count++;
        }
        return status;
    }

    @Override
    public boolean doAnalysis(String url) {
        createNewSearchEnginePack();

        boolean status = !getYears(url); //讀取快照有的年份

        if (status) {
            wap.setError(status);
            return false;
        }

        for (Map.Entry<Integer, List<Long>> item : wap.getSnapshots().entrySet()) {
            status = !getSnapshotsPerYear(url, item.getKey());
            if (status) {
                wap.setError(status);
                return false;
            }
            Collections.sort(item.getValue());
        }

        if (Configure.WEBARCHIVE_MODE == 0) {
            return true;
        }

        for (Map.Entry<Integer, List<Long>> item : wap.getSnapshots().entrySet()) {
            if (wap.getContentKeyword().size() > 3 || wap.getTitleKeyword().size() > 3) {
                break;
            }

            for (Long snapshot : item.getValue()) {
                if (wap.getContentKeyword().size() > 3 || wap.getTitleKeyword().size() > 3) {
                    break;
                }
                status = this.getPage(url, snapshot);
                boolean filter[] = doFilter(snapshot);
                Tools.sleep(100, 2000);
            }

        }
        return true;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean[] doFilter(Long snapshot) {
        boolean status[] = {true, true};
        String title = page.getTitleText();
        String content = page.asText();
        if (Configure.WEBARCHIVE_TITLE_FILTER) {
            for (String s : listTitle) {
                if (title.contains(s)) {
                    status[0] = false;
                    wap.getTitleKeyword().put(snapshot, s);
                    break;
                }
            }
        }
        if (Configure.WEBARCHIVE_CONTENT_FILTER) {
            for (String s : listContent) {
                if (content.contains(s)) {
                    status[1] = false;
                    wap.getContentKeyword().put(snapshot, s);
                    break;
                }
            }
        }
        return status;
    }

    private int timeConvert(Long t) {
        String tmp = t.toString();
        int year = Integer.parseInt(tmp.substring(0, 4));
        int month = Integer.parseInt(tmp.substring(4, 6));
        int day = Integer.parseInt(tmp.substring(6, 8));
        int hours = Integer.parseInt(tmp.substring(8, 10));
        int minute = Integer.parseInt(tmp.substring(10, 12));
        int second = Integer.parseInt(tmp.substring(12));
        return Integer.parseInt(String.format("%d%02d", year, month));
    }

    private boolean getYears(String url) {
        String s = "";
        boolean status = false;
        int count = 0;
        while (!status && count < 15) {
            count++;
            try {
                s = Tools.getJSON(String.format("https://web.archive.org/__wb/sparkline?url=%s", url), 9000);
                if (s.equals("")) {
                    System.out.printf("取讀 %s 年代參數．．．．．失敗。\r\n", url);
                    continue;
                }

                status = true;
                System.out.printf("取讀 %s 年代參數．．．．．成功。\r\n", url);
            } catch (Exception ex) {
                //Logger.getLogger(WebArchiveFilter.class.getName()).log(Level.SEVERE, null, ex);
                System.out.printf("取讀 %s 年代參數．．．．．失敗。\r\n", url);
                Tools.sleep(200, 1000);
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
            Iterator<String> keys = jsonYears.keys();
            while (keys.hasNext()) {
                wap.getSnapshots().put(Integer.parseInt(keys.next()), new ArrayList<Long>());
            }
            Thread.sleep(1, 200);
        } catch (Exception ex) {
            //Logger.getLogger(WebArchiveFilter.class.getName()).log(Level.SEVERE, null, ex);
            //ex.printStackTrace();
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
                s = Tools.getJSON(String.format("http://web.archive.org/__wb/calendarcaptures?url=%s&selected_year=%d", url, year), 9000);
                if (s.equals("")) {
                    System.out.printf("取得 %s %d 年快照參數．．．．．失敗。\r\n", url, year);
                    continue;
                }

                status = true;
                System.out.printf("取得 %s %d 年快照參數．．．．．成功。\r\n", url, year);
            } catch (Exception ex) {
                //Logger.getLogger(WebArchiveFilter.class.getName()).log(Level.SEVERE, null, ex);
                System.out.printf("取得 %s %d 年快照參數．．．．．失敗。\r\n", url, year);
                Tools.sleep(200, 1000);
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
                    analysisJson(secArray.getJSONArray(j), map);
                }
            }
        } catch (Exception e) {
            System.out.printf("%s %d 年快照參數無法分析。 \n", url, year);
        }
        if (map.size() > 0) {
            tmp.addAll(map.values());
        }
        return true;
    }

    private void analysisJson(JSONArray jsonA, Map<Integer, Long> map) {
        try {
            for (int i = 0; i < jsonA.length(); i++) {
                if (jsonA.isNull(i)) {
                    continue;
                }
                JSONObject jsonO = jsonA.getJSONObject(i);
                if (jsonO.has("ts")) {
                    JSONArray ts = jsonO.getJSONArray("ts");
                    for (int j = 0; j < ts.length(); j++) {
                        if (Configure.WEBARCHIVE_MODE == 0) {
                            map.put(map.size() + 1, ts.getLong(j));
                        } else {
                            long snapshot = ts.getLong(j);
                            int key = this.timeConvert(snapshot);
                            if (!map.containsKey(key)) {
                                map.put(key, snapshot);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
