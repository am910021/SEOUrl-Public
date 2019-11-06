/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread;

import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import seourl.data.UrlDataSet;
import seourl.filter.JumingFilter;
import seourl.pack.JumingPack;
import seourl.thread.ex.ControllerAbstract;
import seourl.type.Filter;

/**
 *
 * @author Yuri
 */
public class JumingController extends ControllerAbstract<UrlDataSet>  {

   @Getter
    private Map<String, JumingPack> mJP = new TreeMap<>();

    public JumingController(int pid, UrlDataSet dataSet) {
        super(pid, Filter.JUMING, dataSet);
    }

    @Override
    public void run() {
        JumingFilter j = new JumingFilter(pid);
        j.setCookiePath("cache/Juming/");
        j.setCookie(pid + "-cookie.bin");
        j.loadCookie();
        j.loadWeb("http://www.juming.com");
        j.login();
        j.saveCookie();
        String url;
        while (dsa.hasNext()) {
            url = dsa.getNext();
            j.doAnalysis(url);
            mJP.put(url, j.getJp());
        }
        j.saveCookie();
        j.close();
    }
}
