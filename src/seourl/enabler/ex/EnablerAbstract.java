/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.enabler.ex;

import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import seourl.data.ex.DataSetAbstract;
import seourl.pack.ex.PackAbstract;

/**
 *
 * @author Yuri
 */
public abstract class EnablerAbstract extends Thread {

    @Getter
    protected Map<String, PackAbstract> packMap = new TreeMap<>();   //儲存收集的資料
    @Getter
    protected DataSetAbstract dsa;

    /**
     * This method only change dsa varible once. When object dsa is null, then
     * it will do.
     *
     * @param dsa
     */
    public void setDsa(DataSetAbstract dsa) {
        if (this.dsa == null) {
            this.dsa = dsa;
        }
    }

    @Override
    public abstract void run();
}
