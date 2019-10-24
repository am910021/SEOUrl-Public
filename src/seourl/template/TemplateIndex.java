/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.template;

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

    private String getLink(String[] record) {
        if (record[1].equals("未啟用")) {
            return String.format("<a>%s</a>", record[1]);
        }
        return String.format("<a href=\"%s\" target=\"_blank\">%s</a>", record[0], record[1]);
    }

    public void insertRecord(String url, 
            String[] wap,
            String juming,
            String[] baiduD,
            String[] baiduS,
            String[] So360Se,
            String[] So360Si,
            String[] SogouD,
            String[] SogouS) {
        this.insertByKey("record", url,
                getLink(wap),
                juming,
                getLink(baiduD),
                getLink(baiduS),
                getLink(So360Se),
                getLink(So360Si),
                getLink(SogouD),
                getLink(SogouS));
    }

}
