/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter;

import com.gargoylesoftware.htmlunit.html.DomElement;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import seourl.filter.ex.FilterAbstract;
import seourl.pack.SogouDomainPack;

/**
 *
 * @author yuri
 */
public class SogouDomainFilter extends FilterAbstract {

    @Setter
    List<String> lkeyWords;

    @Getter
    SogouDomainPack sdp = new SogouDomainPack();

    public SogouDomainFilter() {
        super();
        //https://www.sogou.com/web?query="qwertyuio.com"&_ast=1571378276&_asf=www.sogou.com&w=01029901&p=40040100&dp=1&cid=&s_from=result_up&sut=5872&sst0=1571378350786&lkt=13%2C1571378346270%2C1571378350090&sugsuv=1571101180865023&sugtime=1571378350786
        //https://www.sogou.com/web?query="chinaqscm.com"&_ast=1571378350&_asf=www.sogou.com&w=01029901&p=40040100&dp=1&cid=&s_from=result_up&sut=28925&sst0=1571378449573&lkt=0%2C0%2C0&sugsuv=1571101180865023&sugtime=1571378449573
    }

    @Override
    public boolean doAnalysis(String url) {
        try {
            page = webClient.getPage(url);

        } catch (Exception e) {

        }
        List<DomElement> tmp = page.getByXPath("//div[@class='results']/div[@class='vrwrap']");
        int maxPage = 1;
        DomElement p = page.getElementById("pagebar_container");
        if (p != null) {
            maxPage = p.asText().split("\n").length;
            if (maxPage >= 3) {
                maxPage = 3;
            }
        }

        String tmp2;

        System.out.println(maxPage);

//        for (DomElement de : tmp) {
//            tmp2 = de.asText();
//            for (String keyword : lkeyWords) {
//                if (tmp2.indexOf(keyword) >= 0) {
//                    this.sdp.setIllegal(true);
//                }
//            }
//        }
        if (tmp.size() == 0) {
            return false;
        }

        return true;
    }

}
