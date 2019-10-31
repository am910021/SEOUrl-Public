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
public class BaiduSitePack extends SearchEnginePack {

    public BaiduSitePack(String url) {
        super("files/BaiduSite/", "百度搜尋",url);
        this.url = Configure.BAIDU_SITE;
    }

}
