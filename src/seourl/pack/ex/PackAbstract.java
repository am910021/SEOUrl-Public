/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.pack.ex;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author yuri
 */
public abstract class PackAbstract {

    public void print(String url) {
        System.out.println(url + "  " + this.toString());
    }

}
