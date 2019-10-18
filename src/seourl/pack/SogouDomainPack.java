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
 * @author yuri
 */
public class SogouDomainPack {

    @Getter
    @Setter
    private boolean illegal = false;

    private boolean page[] = {false, false, false};

    public void setPage(int i) {
        page[i] = true;
    }

    public String getPage() {
        String out = "";
        if (page[0]) {
            out += "1 ";
        }
        if (page[1]) {
            out += "2 ";
        }
        if (page[2]) {
            out += "3 ";
        }

        return out;
    }

}
