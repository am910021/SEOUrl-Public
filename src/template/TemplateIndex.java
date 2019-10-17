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

    public void insertRecord(String url, String name, int size) {
        this.insertByKey("record", url, name, size, "無");
    }

}
