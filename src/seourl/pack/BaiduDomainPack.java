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
public class BaiduDomainPack extends SearchEnginePack {

    public BaiduDomainPack(String url) {
        super("files/BaiduDomain/", "百度域名", url);
        this.url = Configure.BAIDU_DOMAIN;
    }

}
