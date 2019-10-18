/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package template;

import java.util.Date;
import seourl.Tools;
import seourl.pack.WebArchivePack;

/**
 *
 * @author Yuri
 */
public class TemplateIndex extends Template {

    public TemplateIndex(Date startTime) {
        super("index", startTime);
    }

    public void insertTime(Date time) {
        this.insertByKey("time", Tools.getFormatDate1(time));
    }

    public void insertTime(long time) {
        this.insertByKey("time", Tools.getFormatDate1(time));
    }

    public void insertRecord(String path, String url, WebArchivePack wap, String s1) {
        this.insertByKey("record", path, url, wap.getTotalSize(), (wap.isError() ? "查詢錯誤" : ""), url, s1);
    }

}
