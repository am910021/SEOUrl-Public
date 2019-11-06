/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread.ex;

import java.util.List;
import seourl.data.UrlDataSet;
import seourl.filter.ex.SearchEngineFilterAbstract;
import seourl.type.Filter;

/**
 *
 * @author Yuri
 */
public abstract class SearchEngineControllerAbstract extends ControllerAbstract<UrlDataSet> {

    final protected List<String> keywords;
    protected SearchEngineFilterAbstract sea = null;


    protected abstract void createFilter();

    public SearchEngineControllerAbstract(int pid, Filter f, UrlDataSet dsa, List<String> keywords) {
        super(pid, f, dsa);
        this.keywords = keywords;
    }

    @Override
    public void run() {
        this.createFilter();
        sea.setCookiePath("cache/" + this.filter.getType() + "/");
        sea.setCookie(pid + "-cookie.bin");
        sea.loadCookie();
        String url;
        while (dsa.hasNext()) {
            url = dsa.getNext();
            sea.doAnalysis(url);
            packMap.put(url, sea.getSep());
            sea.getSep().saveFile();
        }
        sea.saveCookie();
        sea.close();
    }

}
