/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import seourl.data.UrlDataSet;
import seourl.filter.SogouSearchFilter;
import seourl.pack.SogouSerachPack;
import seourl.thread.ex.ControllerAbstract;
import seourl.type.Filter;

/**
 *
 * @author Yuri
 */
public class SogouSearchController extends ControllerAbstract<UrlDataSet> {

    private List<String> keywords;
    @Getter
    private Map<String, SogouSerachPack> mSDP = new TreeMap<>();

    public SogouSearchController(int pid, UrlDataSet dataSet, List<String> keywords) {
        super(pid, Filter.SOGOU_SEARCH, dataSet);
        this.keywords = keywords;
    }

    @Override
    public void run() {
        SogouSearchFilter s = new SogouSearchFilter(pid, keywords);
        s.setCookiePath("cache/Sogou-Search/");
        s.setCookie(pid + "-cookie.bin");
        s.loadCookie();
        String url;
        while (dsa.hasNext()) {
            url = dsa.getNext();
            s.doAnalysis(url);
            mSDP.put(url, s.getSSP());
            s.getSSP().saveFile();
        }
        s.saveCookie();
        s.close();
    }
}
