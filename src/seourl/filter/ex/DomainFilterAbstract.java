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

    private final String filterType;
    @Setter
    protected List<String> lkeyWords = new ArrayList<>();
    @Getter
    protected DomainPack dp = new DomainPack();

    public DomainFilterAbstract(String filterType) {
        super();
        this.filterType = filterType;
        System.out.printf("建立 %s 過濾器\n", filterType);
    }

    protected abstract String getPageUrl(String url, int i);

    protected abstract List<DomElement> getResultList();

    protected abstract int getMaxPage();

    @Override
    public boolean doAnalysis(String url) {
        boolean pageError = false;
        //如果讀取第一頁錯誤，取消該網域的分析 並回傳false表示未完成分析
        pageError = !getPage(url, 1);
        if (pageError) {
            dp.setError(1, pageError);
            return false;
        }

        List<DomElement> tmp = this.getResultList();
        //如果沒有任何資料，取消該網域的分析 並回傳true表示已完成分析
        if (tmp.size() == 0) {
            return true;
        }

        //取得總頁數 最多3頁
        int maxPage = this.getMaxPage();

        //分析第1頁
        boolean pass = false;
        pass = doAnalysisContent(tmp, url);
        System.out.printf("%s %s 第%d頁 分析完成。 \n", filterType, url, 1);
        dp.setPage(1, pass);

        //分析第2頁之後
        if (maxPage > 1) {
            for (int i = 2; i <= 3; i++) {
                pageError = !getPage(url, i);
                dp.setError(i, pageError);
                if (pageError) {
                    continue;
                }
                tmp = this.getResultList();
                pass = doAnalysisContent(tmp, url);
                System.out.printf("%s %s 第%d頁 分析完成。 \n", filterType, url, i);
                dp.setPage(i, pass);
            }

        }

        return true; //並回傳true表示已完成分析
    }

    protected boolean getPage(String url, int i) {
        boolean status = false;
        long time = System.currentTimeMillis();
        while ((!status && (System.currentTimeMillis() - time) < Configure.RELOAD_PAGE_TIME * 1000)) {
            try {
                String tUrl = getPageUrl(url, i);
                page = webClient.getPage(tUrl);
                status = true;
                System.out.printf("%s %s 第%d頁 資料讀取功成。 \n", filterType, url, i);
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                System.out.printf("%s %s 第%d頁 資料讀取失敗，重新讀取中。 \n", filterType, url, i);
                Tools.sleep(20, 200);
            }
        }
        if (!status) {
            System.out.printf("%s %s 第%d頁 資料讀取錯誤。 \n", filterType, url, i);
            return false;
        }
        return true;
    }

    private boolean doAnalysisContent(List<DomElement> list, String url) {
        String tmp;
        boolean pageIllegal = false;
        for (DomElement de : list) {
            tmp = de.asText();
            for (String keyword : lkeyWords) {
                if (Configure.DOMAIN_FILTER_MODE == 1) {
                    if (tmp.indexOf(keyword) >= 0) {
                        this.dp.setIllegal(true);
                        pageIllegal = true;
                        break;
                    }
                } else {
                    if ((tmp.indexOf(keyword) >= 0) && (tmp.indexOf(url)>=0)) {
                        this.dp.setIllegal(true);
                        pageIllegal = true;
                        break;
                    }
                }

            }
            if (pageIllegal) {
                break;
            }
        }
        return pageIllegal;
    }

}
