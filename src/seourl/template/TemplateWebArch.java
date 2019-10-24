/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.template;

import java.util.Date;
import seourl.Configure;
import seourl.Tools;

/**
 *
 * @author Yuri
 */
public class TemplateWebArch extends Template {

    public TemplateWebArch(Date startTime) {
        super("webarchive", startTime);
    }

    public void insertTitle(String title) {
        this.insertByKey("title", title);
    }

    public void insertDomain(String domain) {
        this.insertByKey("domainName", domain);
    }

    public void insertTime(long mi) {
        this.insertByKey("time", Tools.getFormatDate1(mi));
    }

    String html = "<tr><td><a href=\"%s\" target=\"_blank\">%d</a></td><td>%s</td><td>%s</td></tr>";

    private String getUrl(long snapshot, String url) {
        return String.format(Configure.WEBARCHIVE, snapshot, url);
    }

    public void insertRecord(long snapshot, String url, String title, String content) {
        this.insertByKey("record", String.format(html, getUrl(snapshot, url), snapshot, title, content));
    }
}
