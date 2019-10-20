/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.pack;

import seourl.Configure;

/**
 *
 * @author yuri
 */
public class SogouSerachPack extends SearchEnginePack {

    public SogouSerachPack() {
        super("files/SogouSerach/", "搜狗搜尋");
        this.url = Configure.SOGOU_SEARCH;
    }

}
