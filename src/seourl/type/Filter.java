/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.type;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Yuri
 */
public enum Filter {
    WEB_ARCHIVE_LIST(0, "WebArchiveList"),
    WEB_ARCHIVE(1, "WebArchive"),
    JUMING(2, "Juming"),
    BAIDU_DOMAIN(3, "Baidu-Domain"),
    BAIDU_SITE(4, "Baidu-Site"),
    SO360_SEARCH(5, "360So-Search"),
    SO360_SITE(6, "360So-Site"),
    SOGOU_DOMAIN(7, "Sogou-Domain"),
    SOGOU_SEARCH(8, "Sogou-Search"),
    DEFAULT(999, "default");

    @Getter
    private int id;

    private String type;

    @Override
    public String toString() {
        return type;
    }

    @Getter
    @Setter
    private boolean enable;

    private Filter(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public static Filter getById(int id) {
        for (Filter f : Filter.values()) {
            if (id == f.getId()) {
                return f;
            }
        }
        return Filter.DEFAULT;
    }

}
