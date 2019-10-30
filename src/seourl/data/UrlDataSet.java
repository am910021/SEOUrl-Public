/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Synchronized;
import seourl.Tools;
import seourl.data.ex.DataSetAbstract;

/**
 *
 * @author Yuri
 */
public class UrlDataSet extends DataSetAbstract {
    
    private List<String> urls = new ArrayList<>();
    private int nextUrl = 0;
    private final Object readUrlLock = new Object();
    
    public void setUrls(List<String> urls) {
        if (this.urls == null || this.urls.size() > 0) {
            return;
        }
        this.urls = urls;
    }
    
    public List<String> getUrlsCopy() {
        List<String> dest = new ArrayList<>(urls);
        return dest;
    }
    
    public int getUrlSize() {
        return urls.size();
    }
    
    @Synchronized("readUrlLock")
    public String getNextUrl() {
        String tmp = urls.get(nextUrl);
        nextUrl++;
        return tmp;
    }
    
    @Synchronized("readUrlLock")
    public boolean hasNextUrl() {
        if (nextUrl >= urls.size()) {
            return false;
        }
        return true;
    }
    
    @Override
    public UrlDataSet getClone() {
        
        try {
            return (UrlDataSet) this.clone();
        } catch (CloneNotSupportedException ex) {
            Tools.printError(this.getClass().getName(), ex);
        }
        return new UrlDataSet();
    }
    
}
