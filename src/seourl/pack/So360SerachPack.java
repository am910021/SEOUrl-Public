/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.pack;

import lombok.ToString;
import seourl.pack.ex.SearchEnginePack;
import seourl.other.Configure;
import seourl.type.Filter;

/**
 *
 * @author yuri
 */
public class So360SerachPack extends SearchEnginePack {

    public So360SerachPack(Filter filter, String url) {
        super(filter, url, Configure.SO360_SEARCH);
    }

}
