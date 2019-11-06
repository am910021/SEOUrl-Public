/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread.ex;

import seourl.data.ex.DataSetAbstract;
import seourl.type.Filter;

/**
 *
 * @author Yuri
 */
public abstract class ControllerAbstract<T extends DataSetAbstract> extends Thread {

    protected final Filter filter;
    protected final T dsa;
    protected final int pid;

    @Override
    public abstract void run();

    public ControllerAbstract(int pid, Filter f, T dsa) {
        this.pid = pid;
        this.filter = f;
        this.dsa = dsa;
    }

    final protected void printProgress() {
        dsa.addProgress();
        if (dsa.isNeedPrintProgress()) {
            System.out.printf("%s讀取參數執行進度 %d / %d \r\n", filter.getType(), dsa.getProgress(), dsa.getSize());
        }
    }
}
