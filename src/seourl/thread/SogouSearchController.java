/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread;

import seourl.data.UrlDataSet;
import seourl.filter.SogouSearchFilter;
import seourl.thread.ex.SearchEngineControllerAbstract;
import seourl.type.Filter;

/**
 *
 * @author Yuri
 */
public class SogouSearchController extends SearchEngineControllerAbstract {

    public SogouSearchController(int pid, UrlDataSet dataSet) {
        super(pid, Filter.SOGOU_SEARCH, dataSet);
        this.needCookie = false;
    }

    @Override
    protected void createFilter() {
        this.sea = new SogouSearchFilter(pid, filter, keywords);
    }
}
