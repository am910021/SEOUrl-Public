/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter.ex;

/**
 *
 * @author yuri
 */
public abstract class BasicFilterAbstract {

    protected final String filterType;
    protected final int pid;

    public BasicFilterAbstract(int pid, String filterType) {
        this.pid = pid;
        this.filterType = filterType;
        System.out.printf("線程-%d 建立 %s 過濾器\n", pid, filterType);
    }

}
