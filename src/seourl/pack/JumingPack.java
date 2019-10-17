/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.pack;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Yuri
 */
public class JumingPack {
    @Getter @Setter
    boolean reg = false;
    @Getter @Setter
    boolean qq = false;
    @Getter @Setter
    boolean weChat = false;
    @Getter @Setter
    boolean gfw = false;
    
    @Getter @Setter
    boolean error = false;
    
    public boolean allPass(){
        return reg && qq && weChat && gfw;
    }
}
