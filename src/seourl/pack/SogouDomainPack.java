/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.pack;

import lombok.ToString;
import seourl.pack.ex.SearchEnginePack;
import seourl.other.Configure;
import seourl.template.TemplateSearchEngine;
import seourl.type.Filter;

/**
 *
 * @author yuri
 */
public class SogouDomainPack extends SearchEnginePack {

    public SogouDomainPack(Filter filter, String domain) {
        super(filter, domain, Configure.SOGOU_DOMAIN + "%22");
    }

    @Override
    public void saveFile() {
        TemplateSearchEngine tse = new TemplateSearchEngine(Configure.startTime);
        tse.setSavePath(this.getFinalPath());
        tse.setSaveName(domain);
        tse.insertTitle(filter.toString(), domain);
        tse.insertType(filter.toString());
        tse.insertDomain(domain);
        tse.insertTime(this.getReadTime());
        tse.insertRecord(url + domain + "%22", domain, this.getError(), this.getPage(), this.getKeyWord());
        tse.creatFile();
    }
}
