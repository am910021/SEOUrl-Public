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
    final String url;
    @Getter
    @Setter
    private boolean urlErrpr = false;
    @Getter
    private Map<Integer, Boolean> yearError = new TreeMap<>(Collections.reverseOrder());
    @Getter
    private Map<Long, Boolean> error = new TreeMap<>(Collections.reverseOrder());
    @Getter
    private Map<Integer, List<Long>> snapshots = new TreeMap<>(Collections.reverseOrder());
    @Getter
    private Map<Long, String> titleKeyword = new TreeMap<>(Collections.reverseOrder());
    @Getter
    private Map<Long, String> contentKeyword = new TreeMap<>(Collections.reverseOrder());

    private String domain = "";

    private Long readTime = System.currentTimeMillis();

    public WebArchivePack(String url) {
        super("files/WebArchive/");
        this.url = url;
    }

    public boolean allPass() {
        return snapshots.size() > 0 && this.titleKeyword.size() == 0 && this.contentKeyword.size() == 0 && !urlErrpr && yearError.size() == 0 && error.size() == 0;
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
        if (urlErrpr && yearError.size() >= 0 && error.size() >= 0) {
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
        tWebArch.setSavePath(getFinalPath());
        tWebArch.setSaveName(url);
        tWebArch.insertTitle(url);
        tWebArch.insertDomain(url);
        tWebArch.insertTime(readTime);
        String title = "通過";
        String content = "通過";
        for (Map.Entry<Integer, List<Long>> entry : snapshots.entrySet()) {
            for (long snapshot : entry.getValue()) {

                if (allPass()) {
                    tWebArch.insertRecord(snapshot, url, title, content);
                } else {
                    if (Configure.WEBARCHIVE_MODE == 1 && Configure.WEBARCHIVE_TITLE_FILTER) {
                        if (this.titleKeyword.containsKey(snapshot)) {
                            title = "<snap style=\"color:red\">" + this.titleKeyword.get(snapshot) + "</snap>";
                        } else {
                            title = "通過";
                        }
                    } else {
                        title = "未啟用";
                    }

                    if (Configure.WEBARCHIVE_MODE == 1 && Configure.WEBARCHIVE_CONTENT_FILTER) {
                        if (this.contentKeyword.containsKey(snapshot)) {
                            content = "<snap style=\"color:red\">" + this.contentKeyword.get(snapshot) + "</snap>";
                        } else {
                            content = "通過";
                        }
                    } else {
                        title = "未啟用";
                    }
                    tWebArch.insertRecord(snapshot, url, title, content);
                }
            }
        }
        tWebArch.creatFile();
    }

}
