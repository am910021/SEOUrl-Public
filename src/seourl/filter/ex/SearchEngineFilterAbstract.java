/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter.ex;

import com.gargoylesoftware.htmlunit.html.DomElement;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import seourl.other.Configure;
import seourl.other.Tools;
import seourl.pack.ex.SearchEnginePack;
import seourl.type.Filter;

/**
 *
 * @author yuri
 */
public abstract class SearchEngineFilterAbstract extends FilterAbstract {

    @Setter
    protected List<String> lkeyWords = new ArrayList<>();
    @Getter
    protected SearchEnginePack sep;

    public SearchEngineFilterAbstract(int pid, Filter filter, List<String> lkeyWords) {
        super(pid, filter);
        this.lkeyWords = lkeyWords;
    }

    protected abstract String getPageUrl(String url, int i);

    protected abstract List<DomElement> getResultList();

    protected abstract int getMaxPage();

    protected abstract boolean doFilter(String tmp, String keyword, String url);

    @Override
    protected abstract void createNewSearchEnginePack(String url);

    protected abstract boolean hasPageError();

    @Override
    public boolean doAnalysis(String url) {
        cleanMemory();
        this.createNewSearchEnginePack(url);
        boolean pageError = false;
        //如果讀取第一頁錯誤，取消該網域的分析 並回傳false表示未完成分析

        pageError = !getPage(url, 1) || this.hasPageError();
        if (pageError) {
            sep.setError(1, pageError);
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
        pass = doAnalysisContent(tmp, url, 1);
        System.out.printf("%s %s 第%d頁 分析完成。 \n", filter, url, 1);
        sep.setPage(1, pass);

        //分析第2頁之後
        if (maxPage > 1) {
            for (int i = 2; i <= 3; i++) {
                pageError = !getPage(url, i);
                sep.setError(i, pageError);
                if (pageError) {
                    continue;
                }
                tmp = this.getResultList();
                pass = doAnalysisContent(tmp, url, i);
                System.out.printf("%s %s 第%d頁 分析完成。 \n", filter, url, i);
                sep.setPage(i, pass);
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
                if (Configure.DEBUG) {
                    System.out.println(tUrl);
                }
                this.cleanMemory();
                page = webClient.getPage(tUrl);
                status = true;
                System.out.printf("%s %s 第%d頁 資料讀取功成。 \n", filter, url, i);
            } catch (Exception ex) {
                Tools.printError(filter, ex);
                System.out.printf("%s %s 第%d頁 資料讀取失敗，重新讀取中。 \n", filter, url, i);
                Tools.sleep(20, 200);
            }
        }
        if (!status) {
            System.out.printf("%s %s 第%d頁 資料讀取錯誤。 \n", filter, url, i);
            return false;
        }
        return true;
    }

    protected boolean doAnalysisContent(List<DomElement> list, String url, int index) {
        String tmp;
        boolean pageIllegal = false;
        for (DomElement de : list) {
            tmp = de.asText();
            for (String keyword : lkeyWords) {
                pageIllegal = this.doFilter(tmp.toUpperCase(), keyword, url);
                if (pageIllegal) {
                    this.sep.setKeyWord(index, keyword);
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
