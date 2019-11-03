/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread.ex;

import lombok.Setter;
import seourl.data.ex.DataSetAbstract;
import seourl.type.Filter;

/**
 *
 * @author Yuri
 */
public abstract class ControllerAbstract extends Thread {

    protected final Filter filter;
    protected final DataSetAbstract dsa;

    @Override
    public abstract void run();

    public ControllerAbstract(Filter f, DataSetAbstract dsa) {
        this.filter = f;
        this.dsa =dsa;
    }

    final protected void printProgress() {
        dsa.addProgress();
        if (dsa.isNeedPrintProgress()) {
            System.out.printf("%s讀取參數執行進度 %d / %d \r\n", filter.getType(), dsa.getProgress(), dsa.getSize());
        }
    }
}
