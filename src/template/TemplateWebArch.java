/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package template;

import java.util.Date;
import seourl.Tools;

/**
 *
 * @author Yuri
 */
public class TemplateWebArch extends Template {
    
    public TemplateWebArch(Date startTime) {
        super("webarchive", startTime);
    }
    
    public void insertTitle(String title){
        this.insertByKey("title", title);
    }
    
    public void insertDomain(String domain){
        this.insertByKey("domainName", domain);
    }
    
    public void insertTime(long mi){
        this.insertByKey("time", Tools.getFormatDate1(mi));
    }
    
    public void insertRecord(long snapshot, String url){
        this.insertByKey("record", snapshot, url, snapshot);
    }
}
