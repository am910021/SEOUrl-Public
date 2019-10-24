/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.pack.ex;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author yuri
 */
public abstract class PackAbstract {

    protected final String path;

    public PackAbstract(String path) {
        this.path = path;
    }

    @Getter
    @Setter
    private long readTime = System.currentTimeMillis();

    public abstract void saveFile(String url, Date startTime);

    public void print(String url) {
        System.out.println(url + "  " + this.toString());
    }

}
