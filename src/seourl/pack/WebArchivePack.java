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
public class WebArchivePack extends PackAbstract {

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

    private String domain = "";

    private Long readTime = System.currentTimeMillis();

    public WebArchivePack() {
        super("files/WebArchive/");
    }

    public boolean allPass() {
        return snapshots.size() > 0 && this.titleKeyword.size() == 0 && this.contentKeyword.size() == 0 && !error;
    }

    public String getReason() {
        String tmp = "";
        if (snapshots.size() == 0) {
            tmp += "無快照 ";
        }
        if (this.titleKeyword.size() > 0) {
            tmp += "標題 ";
        }
        if (this.contentKeyword.size() > 0) {
            tmp += "內容 ";
        }
        if (error) {
            tmp += "錯誤 ";
        }
        return tmp;

    }

    private final String getFinalPath() {
        if (allPass()) {
            return this.path + "pass/";
        } else {
            return this.path + "fail/";
        }
    }

    public final String getSaveLocation() {
        return getFinalPath() + domain + ".html";
    }

    @Override
    public void saveFile(String url, Date startTime) {
        this.domain = url;
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
                title = this.titleKeyword.containsKey(snapshot) ? this.titleKeyword.get(snapshot) : "";
                content = this.contentKeyword.containsKey(snapshot) ? this.contentKeyword.get(snapshot) : "";
                if (titleKeyword.size() > 0 || contentKeyword.size() > 0) {
                    tWebArch.insertRecord(snapshot, url, title, content);
                } else {
                    title = (Configure.WEBARCHIVE_MODE == 1 && Configure.WEBARCHIVE_TITLE_FILTER) ? "通過" : "未啟用";
                    content = (Configure.WEBARCHIVE_MODE == 1 && Configure.WEBARCHIVE_CONTENT_FILTER) ? "通過" : "未啟用";
                    tWebArch.insertRecord(snapshot, url, title, content);
                }

            }
        }
        tWebArch.creatFile();
    }

}
