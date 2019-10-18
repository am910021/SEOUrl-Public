/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter.ex;

import com.gargoylesoftware.htmlunit.html.DomElement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import seourl.Configure;
import seourl.Tools;
import seourl.filter.*;
import seourl.filter.ex.FilterAbstract;
import seourl.pack.DomainPack;

/**
 *
 * @author yuri
 */
public abstract class DomainFilterAbstract extends FilterAbstract {

    @Setter
    List<String> lkeyWords = new ArrayList<>();

    @Getter
    DomainPack dp = new DomainPack();

    
    protected abstract String getPageUrl(String url);
    
    @Override
    public boolean doAnalysis(String url) {
        boolean pageError = false;
        //如果讀取第一頁錯誤，取消該網域的分析 並回傳false表示未完成分析
        pageError = !getPage(url, 1);
        if (pageError) {
            dp.setError(1, pageError);
            return false;
        }

        List<DomElement> tmp = page.getByXPath("//div[@class='results']/div[@class='vrwrap']");
        //如果沒有任何資料，取消該網域的分析 並回傳true表示已完成分析
        if (tmp.size() == 0) {
            return true;
        }

        //取得總頁數 最多3頁
        int maxPage = 1;
        DomElement p = page.getElementById("pagebar_container");
        if (p != null) {
            maxPage = p.asText().split("\n").length;
            if (maxPage >= 3) {
                maxPage = 3;
            }
        }
        //分析第1頁
        boolean pass = false;
        pass = doAnalysisContent(tmp);
        System.out.printf("Sogou-domain %s 第%d頁 分析完成。 \n", url, 1);
        dp.setPage(1, pass);

        //分析第2頁之後
        if (maxPage > 1) {
            for (int i = 2; i <= 3; i++) {
                pageError = !getPage(url, i);
                dp.setError(i, pageError);
                if (pageError) {
                    continue;
                }

                tmp = page.getByXPath("//div[@class='results']/div[@class='vrwrap']");
                pass = doAnalysisContent(tmp);
                System.out.printf("Sogou-domain %s 第%d頁 分析完成。 \n", url, i);
                dp.setPage(i, pass);
            }

        }

        return true; //並回傳true表示已完成分析
    }

    public boolean getPage(String url, int i) {
        String sPage = "";
        if (i > 1) {
            sPage = "&page=" + String.valueOf(i);
        }

        boolean status = false;
        long time = System.currentTimeMillis();
        while ((!status && (System.currentTimeMillis() - time) < Configure.RELOAD_PAGE_TIME * 1000)) {
            try {
                page = webClient.getPage(String.format("https://www.sogou.com/web?query=\"%s\"%s", url, sPage));
                status = true;
                System.out.printf("Sogou-domain %s 第%d頁 資料讀取功成。 \n", url, i);
            } catch (Exception ex) {
                Logger.getLogger(SogouDomainFilter.class.getName()).log(Level.SEVERE, null, ex);
                System.out.printf("Sogou-domain %s 第%d頁 資料讀取失敗，重新讀取中。 \n", url, i);
                Tools.sleep(20, 200);
            }
        }
        if (!status) {
            System.out.printf("Sogou-domain %s 第%d頁 資料讀取錯誤。 \n", url, i);
            return false;
        }
        return true;
    }

    private boolean doAnalysisContent(List<DomElement> list) {
        String tmp;
        boolean pageIllegal = false;
        for (DomElement de : list) {
            tmp = de.asText();
            for (String keyword : lkeyWords) {
                if (tmp.indexOf(keyword) >= 0) {
                    this.dp.setIllegal(true);
                    pageIllegal = true;
                    break;
                }
            }
            if (pageIllegal) {
                break;
            }
        }
        return pageIllegal;
    }

}
