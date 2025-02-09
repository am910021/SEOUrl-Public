/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.pack.ex;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import seourl.other.Configure;
import seourl.template.TemplateSearchEngine;
import seourl.type.Filter;

/**
 *
 * @author yuri
 */
@ToString(callSuper = true)
public class SearchEnginePack extends PackAbstract {

    @Setter
    protected String url;
    

    @Getter
    @Setter
    protected boolean illegal = false;

    protected boolean error[] = {false, false, false};

    protected boolean page[] = {false, false, false};

    protected String keyword[] = {"", "", ""};

    public SearchEnginePack(Filter filter, String domain, String url) {
        super(filter, domain);
        this.url = url;
    }

    public String getStatus() {
        return this.getError() + "\n" + this.getPage() + "\n" + this.getKeyWord();
    }

    public boolean isError() {
        return error[0] || error[1] || error[2];
    }

    public void setKeyWord(int i, String key) {
        keyword[i - 1] = key;
    }

    public String getKeyWord() {
        if (!page[0] && !page[1] && !page[2]) {
            return "";
        }
        String out = "關鍵字：";
        if (page[0]) {
            out += keyword[0] + "、";
        }
        if (page[1]) {
            out += keyword[1] + "、";
        }
        if (page[2]) {
            out += keyword[2] + "、";
        }
        out = out.substring(0, out.length() - 1);
        return out;

    }

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

    @Override
    public boolean allPass() {
        return !this.isError() && !this.isIllegal();
    }

    @Override
    public void saveFile() {
        TemplateSearchEngine tse = new TemplateSearchEngine(Configure.startTime);
        tse.setSavePath(this.getFinalPath());
        tse.setSaveName(domain);
        tse.insertTitle(filter.toString(), domain);
        tse.insertType(filter.toString());
        tse.insertDomain(domain);
        tse.insertTime(this.getReadTime());
        tse.insertRecord(url + domain, domain, this.getError(), this.getPage(), this.getKeyWord());
        tse.creatFile();
    }

}
