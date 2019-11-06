/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread;

import java.util.List;
import seourl.data.UrlDataSet;
import seourl.filter.So360SiteFilter;
import seourl.thread.ex.SearchEngineControllerAbstract;
import seourl.type.Filter;

/**
 *
 * @author Yuri
 */
public class So360SiteController extends SearchEngineControllerAbstract {

    public So360SiteController(int pid, UrlDataSet dataSet, List<String> keywords) {
        super(pid, Filter.SO360_SITE, dataSet, keywords);
    }

    @Override
    protected void createFilter() {
        this.sea = new So360SiteFilter(pid, filter, keywords);
    }
}
