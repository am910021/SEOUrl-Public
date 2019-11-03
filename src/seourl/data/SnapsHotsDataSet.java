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
import seourl.other.TPair;
import seourl.other.Tools;
import seourl.data.ex.DataSetAbstract;

/**
 *
 * @author Yuri
 */
public class SnapsHotsDataSet extends DataSetAbstract<TPair<String, Integer, Long>> {

    private List<TPair<String, Integer, Long>> wash = new ArrayList<>();
    private int nextWASH = 0;
    private final Object readLock = new Object();

    @Override
    public void setData(List<TPair<String, Integer, Long>> list) {
        List<TPair<String, Integer, Long>> tmp = list;
        if (this.wash == null || this.wash.size() > 0) {
            return;
        }
        this.wash = tmp;
    }

    @Override
    public List<TPair<String, Integer, Long>> getListCopy() {
        List<TPair<String, Integer, Long>> dest = new ArrayList<>(wash);
        return dest;
    }

    @Override
    public int getSize() {
        return wash.size();
    }

    @Synchronized("readLock")
    @Override
    public TPair<String, Integer, Long> getNext() {
        TPair<String, Integer, Long> t = wash.get(nextWASH);
        nextWASH++;
        return t;
    }

    @Synchronized("readLock")
    @Override
    public boolean hasNext() {
        if (nextWASH >= wash.size()) {
            return false;
        }
        return true;
    }

    @Override
    public SnapsHotsDataSet getClone() {
        try {
            return (SnapsHotsDataSet) this.clone();
        } catch (CloneNotSupportedException ex) {
            Tools.printError(this.getClass().getName(), ex);
        }
        return new SnapsHotsDataSet();
    }
}
