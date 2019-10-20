/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.pack;

import java.util.Date;
import seourl.Configure;
import seourl.template.TemplateSearchEngine;

/**
 *
 * @author yuri
 */
public class SogouDomainPack extends SearchEnginePack {

    public SogouDomainPack() {
        super("files/SogouDomain/", "搜狗域名");
        this.url = Configure.SOGOU_DOMAIN + "%22";
    }

    @Override
    public void saveFile(String domain, Date startTime) {
        this.domain = domain;
        TemplateSearchEngine tse = new TemplateSearchEngine(startTime);
        tse.setSavePath(this.getFinalPath());
        tse.setSaveName(domain);
        tse.insertTitle(this.type, domain);
        tse.insertType(this.type);
        tse.insertDomain(domain);
        tse.insertTime(this.getReadTime());
        tse.insertRecord(url + domain + "%22", domain, this.getError(), this.getPage(), this.getKeyWord());
        tse.creatFile();
    }
}
