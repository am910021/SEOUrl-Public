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
import seourl.TPair;
import seourl.Tools;
import seourl.data.ex.DataSetAbstract;

/**
 *
 * @author Yuri
 */
public class SnapsHotsDataSet extends DataSetAbstract {

    private List<TPair<String, Integer, Long>> wash = new ArrayList<>();
    private int nextWASH = 0;
    private final Object readLock = new Object();

    public void setWash(List<TPair<String, Integer, Long>> ltp) {
        if (this.wash == null || this.wash.size() > 0) {
            return;
        }
        this.wash = ltp;
    }

    public List<TPair<String, Integer, Long>> getWashCopy() {
        List<TPair<String, Integer, Long>> dest = new ArrayList<>(wash);
        return dest;
    }

    public int getWashSize() {
        return wash.size();
    }

    @Synchronized("readLock")
    public TPair<String, Integer, Long> getNextWASH() {
        TPair<String, Integer, Long> t = wash.get(nextWASH);
        nextWASH++;
        return t;
    }

    @Synchronized("readLock")
    public boolean hasNextWASH() {
        if (nextWASH >= wash.size()) {
            return false;
        }
        return true;
    }

    @Override
    public SnapsHotsDataSet getClone() {
        try {
            return (SnapsHotsDataSet)this.clone();
        } catch (CloneNotSupportedException ex) {
            Tools.printError(this.getClass().getName(), ex);
        }
        return new SnapsHotsDataSet();
    }
}
