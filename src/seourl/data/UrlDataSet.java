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
import seourl.other.Tools;
import seourl.data.ex.DataSetAbstract;

/**
 *
 * @author Yuri
 */
public class UrlDataSet extends DataSetAbstract<String> {

    private List<String> urls = new ArrayList<>();
    private int nextUrl = 0;
    private final Object readUrlLock = new Object();

    @Override
    public void setData(List<String> list) {
        List<String> tmp = list;
        if (this.urls == null || this.urls.size() > 0) {
            return;
        }
        this.urls = tmp;
    }

    @Override
    public List<String> getListCopy() {
        List<String> dest = new ArrayList<>(urls);
        return dest;
    }

    @Override
    public int getSize() {
        return urls.size();
    }
    
    @Synchronized("readUrlLock")
    @Override
    public String getNext() {
        String tmp = urls.get(nextUrl);
        nextUrl++;
        return tmp;
    }

    @Synchronized("readUrlLock")
    public boolean hasNext() {
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
