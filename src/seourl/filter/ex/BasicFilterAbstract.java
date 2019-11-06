/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter.ex;

import seourl.type.Filter;

/**
 *
 * @author yuri
 */
public abstract class BasicFilterAbstract {

    protected final Filter filter;
    protected final int pid;

    public BasicFilterAbstract(int pid, Filter filter) {
        this.pid = pid;
        this.filter = filter;
        System.out.printf("線程-%d 建立 %s 過濾器\n", pid, filter.getType());
    }

}
