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
public class BaiduSitePack extends SearchEnginePack {

    public BaiduSitePack() {
        super("files/BaiduSite/", "百度搜尋");
        this.url = Configure.BAIDU_SITE;
    }

}
