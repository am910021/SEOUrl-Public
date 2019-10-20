/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.template;

import java.util.Date;
import seourl.Tools;

/**
 *
 * @author Yuri
 */
public class TemplateSearchEngine extends Template {

    public TemplateSearchEngine(Date startTime) {
        super("searchEngine", startTime);
    }

    public void insertTitle(String title, String domain) {
        this.insertByKey("title", title, domain);
    }

    public void insertType(String type) {
        this.insertByKey("type", type);
    }

    public void insertDomain(String domain) {
        this.insertByKey("domainName", domain);
    }

    public void insertTime(long mi) {
        this.insertByKey("time", Tools.getFormatDate1(mi));
    }

    public void insertRecord(String url, String domain, String error, String page, String keyword) {
        this.insertByKey("record", url, domain, error, page, keyword);
    }
}
