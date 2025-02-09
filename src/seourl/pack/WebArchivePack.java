/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.pack;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import seourl.other.Configure;
import seourl.pack.ex.PackAbstract;
import seourl.template.TemplateWebArch;
import seourl.type.Filter;

/**
 *
 * @author yuri
 */
public class WebArchivePack extends PackAbstract {

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

    private final static String green = "<snap style=\"color:green\">%s</snap>";
    private final static String red = "<snap style=\"color:red\">%s</snap>";

    public WebArchivePack(Filter filter, String domain) {
        super(filter, domain);
    }

    @Override
    public boolean allPass() {
        if (snapshots.size() == 0) {
            return false;
        }
        if (this.titleKeyword.size() > 0) {
            return false;
        }
        if (this.contentKeyword.size() > 0) {
            return false;
        }
        if (urlErrpr) {
            return false;
        }
        if (yearError.size() > 0) {
            return false;
        }
        if (error.size() > 0) {
            return false;
        }

        return true;
    }

    @Override
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

    @Override
    public void saveFile() {
        TemplateWebArch tWebArch = new TemplateWebArch(Configure.startTime);
        tWebArch.setSavePath(getFinalPath());
        tWebArch.setSaveName(domain);
        tWebArch.insertTitle(domain);
        tWebArch.insertDomain(domain);
        tWebArch.insertTime(this.getReadTime());
        String title = "未啟用";
        String content = "未啟用";
        for (Map.Entry<Integer, List<Long>> entry : snapshots.entrySet()) {
            for (long snapshot : entry.getValue()) {
                if (!Configure.WEBARCHIVE_TITLE_FILTER && !Configure.WEBARCHIVE_CONTENT_FILTER) {
                    tWebArch.insertRecord(snapshot, domain, title, content);
                    continue;
                }

                if (Configure.WEBARCHIVE_TITLE_FILTER) {
                    if (this.titleKeyword.containsKey(snapshot)) {
                        title = String.format(red, this.titleKeyword.get(snapshot));
                    } else {
                        title = String.format(green, "通過");
                    }
                }

                if (Configure.WEBARCHIVE_CONTENT_FILTER) {
                    if (this.contentKeyword.containsKey(snapshot)) {
                        content = String.format(red, this.contentKeyword.get(snapshot));
                    } else {
                        content = String.format(green, "通過");
                    }
                }
                tWebArch.insertRecord(snapshot, domain, title, content);
            }
        }
        tWebArch.creatFile();
    }

}
