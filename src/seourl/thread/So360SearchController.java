/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread;

import java.util.List;
import seourl.data.UrlDataSet;
import seourl.filter.So360SearchFilter;
import seourl.thread.ex.SearchEngineControllerAbstract;
import seourl.type.Filter;

/**
 *
 * @author Yuri
 */
public class So360SearchController extends SearchEngineControllerAbstract {

    public So360SearchController(int pid, UrlDataSet dataSet) {
        super(pid, Filter.SO360_SEARCH, dataSet);
    }

    @Override
    protected void createFilter() {

        this.sea = new So360SearchFilter(pid, filter, keywords);
    }
}
