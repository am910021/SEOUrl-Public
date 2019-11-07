/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.pack.ex;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import seourl.type.Filter;

/**
 *
 * @author yuri
 */
@ToString
public abstract class PackAbstract implements Serializable {

    protected final Filter filter;
    protected final String domain;

    public PackAbstract(Filter filter, String domain) {
        this.filter = filter;
        this.domain = domain;
    }

    @Getter
    @Setter
    private long readTime = System.currentTimeMillis();

    public abstract boolean allPass();

    public abstract void saveFile();

    public String getReason() {
        return "";
    }

    public String[] getIndexStr() {
        String tmp[] = {this.getSaveLocation(), this.allPass() ? "通過" : "未通過 " + this.getReason()};
        return tmp;
    }

    public final String getSaveLocation() {
        return getFinalPath() + domain + ".html";
    }

    protected final String getFinalPath() {
        if (allPass()) {
            return "files/" + filter.toString() + "pass/";
        } else {
            return "files/" + filter.toString() + "fail/";
        }
    }

    public void print(String url) {
        System.out.println(this.toString());
    }

}
