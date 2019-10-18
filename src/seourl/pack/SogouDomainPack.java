/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.pack;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import seourl.pack.ex.PackAbstract;

/**
 *
 * @author yuri
 */
@ToString
public class SogouDomainPack extends PackAbstract {

    @Getter
    @Setter
    private boolean illegal = false;

    private boolean error[] = {false, false, false};

    private boolean page[] = {false, false, false};

    public void setPage(int i, boolean b) {
        page[i - 1] = b;
    }

    public String getPage() {
        if (!page[0] && !page[1] && !page[2]) {
            return "";
        }
        String out = "第";
        if (page[0]) {
            out += "1、";
        }
        if (page[1]) {
            out += "2、";
        }
        if (page[2]) {
            out += "3、";
        }
        out = out.substring(0, out.length() - 1);
        out += "頁違規";
        return out;
    }

    public void setError(int i, boolean b) {
        error[i - 1] = b;
    }

    public String getError() {
        if (!error[0] && !error[1] && !error[2]) {
            return "";
        }
        String out = "第";
        if (error[0]) {
            out += "1、";
        }
        if (error[1]) {
            out += "2、";
        }
        if (error[2]) {
            out += "3、";
        }
        out = out.substring(0, out.length() - 1);
        out += "頁錯誤";
        return out;
    }

}
