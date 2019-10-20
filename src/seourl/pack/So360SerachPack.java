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
public class So360SerachPack extends SearchEnginePack {

    public So360SerachPack() {
        super("files/So360Serach/", "360搜尋");
        this.url = Configure.SO360_SEARCH;
    }

}
