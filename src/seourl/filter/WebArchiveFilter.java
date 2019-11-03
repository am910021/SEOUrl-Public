/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import seourl.other.Configure;
import seourl.other.Tools;
import seourl.filter.ex.BasicFilterAbstract;
import seourl.pack.WebArchivePack;

/**
 *
 * @author Yuri
 */
public class WebArchiveFilter extends BasicFilterAbstract {

    @Getter
    @Setter
    private WebArchivePack wap;

    private List<String> listTitle;
    private List<String> listContent;

    Document doc;
    final int pid;

    public WebArchiveFilter(int pid, List<String> listTitle, List<String> listContent) {
        super("WebArchive");
        this.listTitle = listTitle;
        this.listContent = listContent;
        this.pid = pid;
    }

    public boolean getPage(String url, long s) {
        boolean status = false;
        int count = 0;
        final int baseTimeout = 30 * 1000;
        int timeout = 0;
        String tmpUrl = String.format(Configure.WEBARCHIVE, s, url);
        while ((!status && count < Configure.WEBARCH_TRY_REDECT_TIMES)) {
            timeout += baseTimeout;
            try {
                
                doc = Tools.getUrlDocument(tmpUrl, timeout);
                if (doc == null) {
                    throw new Exception("doc is null");
                }
                Element element = doc.select("p[class=impatient] > a").first();
                if (element != null) {
                    tmpUrl = element.absUrl("href");
                    doc = null;
                    doc = Tools.getUrlDocument(tmpUrl, timeout);
                }

                if (doc == null) {
                    throw new Exception("doc is null");
                }
                status = true;
                System.out.printf("線程-%d 取得 %s %d 快照成功。 \r\n",pid, url, s);
            } catch (Exception ex) {
                Tools.printError(filterType, ex);
                System.out.printf("線程-%d 取得 %s %d 快照失敗。\r\n",pid, url, s);
                Tools.sleep(1*1000, 5*1000);
            }
            count++;
        }
        return status;
    }

    public boolean doAnalysis(String url, long s) {
        boolean pageError = !getPage(url, s);
        if (pageError) {
            wap.getError().put(s, true);
            wap.print(url);
            return false;
        }
        boolean status[] = doFilter(s);

        return true;

    }

    private boolean[] doFilter(long snapshot) {
        boolean status[] = {true, true};
        String title = doc.title();
        String content = doc.body().text();

        if (Configure.WEBARCHIVE_TITLE_FILTER) {
            for (String s : listTitle) {
                if (title.contains(s)) {
                    status[0] = false;
                    wap.getTitleKeyword().put(snapshot, s);
                    break;
                }
            }
        }
        if (Configure.WEBARCHIVE_CONTENT_FILTER) {
            for (String s : listContent) {
                if (content.contains(s)) {
                    status[1] = false;
                    wap.getContentKeyword().put(snapshot, s);
                    break;
                }
            }
        }
        return status;
    }
}
