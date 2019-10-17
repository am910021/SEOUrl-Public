/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.pack;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import template.Template;
import seourl.Tools;
import template.TemplateWebArch;

/**
 *
 * @author yuri
 */
public class WebArchivePack {

    @Getter
    @Setter
    private long readTime;
    @Getter
    private Map<Integer, List<Long>> snapshots = new HashMap<>();
    @Getter
    private int totalSize = 0;

    public void addTotalSize(int i) {
        this.totalSize += i;
    }

    public void put(Integer index, List<Long> list) {
        this.snapshots.put(index, list);
    }

    public void saveFile(String url, Date startTime) {
        TemplateWebArch tWebArch = new TemplateWebArch(startTime);
        tWebArch.setSavePath("files");
        tWebArch.setSaveName(url);
        tWebArch.insertTitle( url);
        tWebArch.insertDomain(url);
        tWebArch.insertTime(readTime);
        for (Map.Entry<Integer, List<Long>> entry : snapshots.entrySet()) {
            for (long snapshot : entry.getValue()) {
                tWebArch.insertRecord(snapshot, url);
            }
        }
        tWebArch.creatFile();
    }


}
