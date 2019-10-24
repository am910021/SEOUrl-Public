/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.pack;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import seourl.Configure;
import seourl.pack.ex.PackAbstract;
import seourl.template.TemplateWebArch;

/**
 *
 * @author yuri
 */
@ToString
public class WebArchivePack2 extends PackAbstract {

    @Getter
    @Setter
    boolean error = false;
    @Getter
    @Setter
    private Map<Integer, List<Long>> snapshots = new TreeMap<>(Collections.reverseOrder());
    @Getter
    @Setter
    private Map<Long, String> titleKeyword = new TreeMap<>(Collections.reverseOrder());
    @Getter
    @Setter
    private Map<Long, String> contentKeyword = new TreeMap<>(Collections.reverseOrder());

    private Long readTime = System.currentTimeMillis();

    @Override
    public void saveFile(String url, Date startTime) {
        TemplateWebArch tWebArch = new TemplateWebArch(startTime);
        if (titleKeyword.size() > 0 || contentKeyword.size() > 0) {
            tWebArch.setSavePath("files/WebArchive/fail/");
        } else {
            tWebArch.setSavePath("files/WebArchive/pass/");
        }
        tWebArch.setSaveName(url);
        tWebArch.insertTitle(url);
        tWebArch.insertDomain(url);
        tWebArch.insertTime(readTime);
        String title;
        String content;
        for (Map.Entry<Integer, List<Long>> entry : snapshots.entrySet()) {
            for (long snapshot : entry.getValue()) {
                title = this.titleKeyword.get(snapshot);
                content = this.contentKeyword.get(snapshot);
                if(titleKeyword.size() > 0 || contentKeyword.size() > 0){
                    tWebArch.insertRecord(0, url, title, content);
                }else{
                    title = (Configure.WEBARCHIVE_MODE ==1 && Configure.WEBARCHIVE_TITLE_FILTER) ? "通過" : "未啟用";
                    content = (Configure.WEBARCHIVE_MODE ==1 && Configure.WEBARCHIVE_CONTENT_FILTER) ? "通過" : "未啟用";
                    tWebArch.insertRecord(0, url, title, content);
                }
              
            }
        }
        tWebArch.creatFile();
    }

}
