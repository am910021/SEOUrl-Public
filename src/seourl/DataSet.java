/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

/**
 *
 * @author Yuri
 */
public class DataSet {

    @Getter
    @Setter
    List<TPair<String, Integer, Long>> ltp = new ArrayList<>();

    private int nextWASH = 0;
   private final Object readLock = new Object();

    @Synchronized("readLock")
    public TPair<String, Integer, Long> getNextWASH() {
        TPair<String, Integer, Long> t = ltp.get(nextWASH);
        nextWASH++;
        return t;
    }

    @Synchronized("readLock")
    public boolean hasNextWASH() {
        if (nextWASH >= ltp.size()) {
            return false;
        }
        return true;
    }

}
