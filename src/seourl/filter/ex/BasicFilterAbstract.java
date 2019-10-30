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
public abstract class BasicFilterAbstract implements FilterInterface{
    protected final String filterType;

    public BasicFilterAbstract(String filterType) {
        this.filterType = filterType;
        System.out.printf("建立 %s 過濾器\n", filterType);
    }
    
    
    
}
