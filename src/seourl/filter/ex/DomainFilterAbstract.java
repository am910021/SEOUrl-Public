/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.filter.ex;

import java.util.List;
import seourl.other.Configure;

/**
 *
 * @author yuri
 */
public abstract class DomainFilterAbstract extends SearchEngineFilterAbstract {

    @Override
    protected abstract void createNewSearchEnginePack(String url);

    @Override
    protected abstract boolean hasPageError();

    public DomainFilterAbstract(String filterType, List<String> lkeyWords) {
        super(filterType, lkeyWords);
    }

    @Override
    final protected boolean doFilter(String tmp, String keyword, String url) {
        boolean pageIllegal = false;
        if (Configure.DOMAIN_FILTER_MODE == 1) {
            if (tmp.contains(keyword)) {
                this.getSep().setIllegal(true);
                pageIllegal = true;
            }
        } else {
            if ((tmp.contains(keyword)) && (tmp.contains(url))) {
                this.getSep().setIllegal(true);
                pageIllegal = true;
            }
        }
        return pageIllegal;
    }

}
