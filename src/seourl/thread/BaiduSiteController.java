/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread;

import java.util.List;
import seourl.data.UrlDataSet;
import seourl.filter.BaiduSiteFilter;
import seourl.thread.ex.SearchEngineControllerAbstract;
import seourl.type.Filter;

/**
 *
 * @author Yuri
 */
public class BaiduSiteController extends SearchEngineControllerAbstract {

    public BaiduSiteController(int pid, UrlDataSet dataSet) {
        super(pid, Filter.BAIDU_SITE, dataSet);
    }

    @Override
    protected void createFilter() {
        this.sea = new BaiduSiteFilter(pid, filter, keywords);
    }
}
