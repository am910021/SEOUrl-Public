/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.data.ex;

import lombok.Synchronized;

/**
 *
 * @author Yuri
 */
public abstract class DataSetAbstract implements Cloneable {

    private int progress = 0;
    private final Object lockProgress = new Object();
    private long nextPrint;

    public DataSetAbstract() {
        nextPrint = System.currentTimeMillis() + 10 * 1000;
    }

    @Synchronized("lockProgress")
    public int getProgress() {
        return this.progress;
    }

    @Synchronized("lockProgress")
    public void addProgress() {
        progress++;
    }

    @Synchronized("lockProgress")
    public boolean isNeedPrintProgress() {
        if (System.currentTimeMillis() > nextPrint) {
            nextPrint = System.currentTimeMillis() + 10 * 1000;
            return true;
        }

        return false;
    }

    public abstract DataSetAbstract getClone();

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
