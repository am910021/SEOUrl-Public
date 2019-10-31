/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.pack;

import seourl.pack.ex.SearchEnginePack;
import seourl.Configure;

/**
 *
 * @author yuri
 */
public class SogouSerachPack extends SearchEnginePack {

    public SogouSerachPack(String url) {
        super("files/SogouSerach/", "搜狗搜尋", url);
        this.url = Configure.SOGOU_SEARCH;
    }

}
