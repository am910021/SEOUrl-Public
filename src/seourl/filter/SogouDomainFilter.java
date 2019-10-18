/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.DomElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import seourl.Configure;
import seourl.Tools;
import seourl.filter.ex.FilterAbstract;
import seourl.pack.SogouDomainPack;

/**
 *
 * @author yuri
 */
public class SogouDomainFilter extends FilterAbstract {

    @Setter
    List<String> lkeyWords = new ArrayList<>();

    @Getter
    SogouDomainPack sdp = new SogouDomainPack();

    public SogouDomainFilter() {
        super();
        //https://www.sogou.com/web?query="qwertyuio.com"&_ast=1571378276&_asf=www.sogou.com&w=01029901&p=40040100&dp=1&cid=&s_from=result_up&sut=5872&sst0=1571378350786&lkt=13%2C1571378346270%2C1571378350090&sugsuv=1571101180865023&sugtime=1571378350786
        //https://www.sogou.com/web?query="chinaqscm.com"&_ast=1571378350&_asf=www.sogou.com&w=01029901&p=40040100&dp=1&cid=&s_from=result_up&sut=28925&sst0=1571378449573&lkt=0%2C0%2C0&sugsuv=1571101180865023&sugtime=1571378449573
    }

    @Override
    public boolean doAnalysis(String url) {
        boolean pageError = false;
        //如果讀取第一頁錯誤，取消該網域的分析 並回傳false表示未完成分析
        pageError = !getPage(url, 1);
        if (pageError) {
            sdp.setError(1, pageError);
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
        sdp.setPage(1, pass);

        
        //分析第2頁之後
        if (maxPage > 1) {
            for (int i = 2; i <= 3; i++) {
                pageError = !getPage(url, i);
                sdp.setError(i, pageError);
                if (pageError) {
                    continue;
                }

                tmp = page.getByXPath("//div[@class='results']/div[@class='vrwrap']");
                pass = doAnalysisContent(tmp);
                System.out.printf("Sogou-domain %s 第%d頁 分析完成。 \n", url, i);
                sdp.setPage(i, pass);
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
                    this.sdp.setIllegal(true);
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
