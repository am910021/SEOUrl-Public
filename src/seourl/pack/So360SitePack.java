/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.pack;

import seourl.pack.ex.SearchEnginePack;
import seourl.other.Configure;

/**
 *
 * @author yuri
 */
public class So360SitePack extends SearchEnginePack {

    public So360SitePack(String url) {
        super("files/So360Site/", "360搜尋網站", url);
        this.url = Configure.SO360_SITE;
    }

}
