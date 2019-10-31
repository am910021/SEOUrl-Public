/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.enabler;

import seourl.enabler.ex.EnablerAbstract;

/**
 *
 * @author Yuri
 */
public class BaiduDomainFilterEnabler extends EnablerAbstract {
    
    private BaiduDomainFilterEnabler() {
    }
    
    public static BaiduDomainFilterEnabler getInstance() {
        return BaiduDomainFilterEnablerHolder.INSTANCE;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private static class BaiduDomainFilterEnablerHolder {

        private static final BaiduDomainFilterEnabler INSTANCE = new BaiduDomainFilterEnabler();
    }
}
