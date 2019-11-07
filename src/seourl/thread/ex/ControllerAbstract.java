/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.thread.ex;

import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import seourl.data.ex.DataSetAbstract;
import seourl.pack.ex.PackAbstract;
import seourl.type.Filter;

/**
 *
 * @author Yuri
 * @param <T> extends DataSetAbstract
 */
public abstract class ControllerAbstract<T extends DataSetAbstract> extends Thread {

    protected final Filter filter;
    protected final T dsa;
    protected final int pid;
    
    @Getter
    protected final Map<String, PackAbstract> packMap;

    @Override
    public abstract void run();

    public ControllerAbstract(int pid, Filter f, T dsa) {
        this.packMap = new TreeMap<>();
        this.pid = pid;
        this.filter = f;
        this.dsa = dsa;
    }

    public ControllerAbstract(int pid, Filter f, T dsa, Map<String, PackAbstract> packMap) {
        this.packMap = packMap;
        this.pid = pid;
        this.filter = f;
        this.dsa = dsa;
    }

    final protected void printProgress() {
        dsa.addProgress();
        if (dsa.isNeedPrintProgress()) {
            System.out.printf("%s讀取參數執行進度 %d / %d \r\n", filter, dsa.getProgress(), dsa.getSize());
        }
    }
}
