/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.other;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Properties;
import lombok.Cleanup;
import seourl.type.Filter;

/**
 *
 * @author yuri
 */
public class Configure {

    public static final Date startTime = new Date();
    public static final InetAddress localAddress;

    public static final String KEY_WORD_PATH = "keyword/";
    public static final int MAX_THREAD = 5; //多線程，聚名網 sogou 專用
    public static final int WEBARCHIVE_MAX_THREAD = 5; //多線程，聚名網 sogou 專用

    public static final String JUMING_FILTER_LOGIN = "http://www.juming.com/";
    public static final String JUMING_FILTER = "http://www.juming.com/hao/?cha_ym=";
    public static final String WEBARCHIVE = "http://web.archive.org/web/%d/http://%s/";
    public static final String BAIDU_DOMAIN = "https://www.baidu.com/s?wd=domain:";
    public static final String BAIDU_SITE = "https://www.baidu.com/s?wd=site:";
    public static final String SO360_SEARCH = "https://www.so.com/s?q=";
    public static final String SO360_SITE = "https://www.so.com/s?q=site:";
    public static final String SOGOU_DOMAIN = "https://www.sogou.com/web?query=";
    public static final String SOGOU_SEARCH = "https://www.sogou.com/web?query=";

    public static final int WEBARCH_TRY_REDECT_TIMES = 3;

    public static final boolean ENABLE_WEBARCHIVE;
    //public static final int WEBARCHIVE_MODE;
    public static final boolean WEBARCHIVE_TITLE_FILTER;
    public static final boolean WEBARCHIVE_CONTENT_FILTER;

    public static final boolean ENABLE_JUMING_FILTER;
    public static final int DOMAIN_FILTER_MODE;
    public static final boolean ENABLE_BAIDU_DOMAIN;
    public static final boolean ENABLE_BAIDU_SITE;
    public static final boolean ENABLE_SO360_SEARCH;
    public static final boolean ENABLE_SO360_SITE;
    public static final boolean ENABLE_SOGOU_DOMAIN;
    public static final boolean ENABLE_SOGOU_SEARCH;
    public static final boolean DEBUG = true;

    private static String comm
            = "#WebArchive過濾 1=啟動 0=關閉\r"
            + "ENABLE_WEBARCHIVE          = 1\r\r"
            + "#WebArchive過濾Title 1=啟動 0=關閉\r"
            + "WEBARCHIVE_TITLE_FILTER    = 1\r\r"
            + "#WebArchive過濾Content 1=啟動 0=關閉\r"
            + "WEBARCHIVE_CONTENT_FILTER  = 1\r\r"
            + "#聚名網過濾 1=啟動 0=關閉\r"
            + "ENABLE_JUMING_FILTER       = 1\r\r"
            + "#1=嚴格  2=寬鬆\r"
            + "DOMAIN_FILTER_MODE         = 1\r\r"
            + "#百度域名 1=啟動 0=關閉\r"
            + "ENABLE_BAIDU_DOMAIN        = 1\r\r"
            + "#百度網站 1=啟動 0=關閉\r"
            + "ENABLE_BAIDU_SITE          = 1\r\r"
            + "#360搜尋  1=啟動 0=關閉\r"
            + "ENABLE_SO360_SEARCH        = 1\r\r"
            + "#360網站  1=啟動 0=關閉\r"
            + "ENABLE_SO360_SITE          = 1\r\r"
            + "#搜狗域名 1=啟動 0=關閉\r"
            + "ENABLE_SOGOU_DOMAIN        = 1\r\r"
            + "#搜狗搜尋 1=啟動 0=關閉\r"
            + "ENABLE_SOGOU_SEARCH        = 1\r\r";

    public static final int RELOAD_PAGE_TIME = 10; //網頁出錯的話，在10秒內重復讀

    static {
        boolean enableWebArchive = true;
        int webArchiveMode = 0;
        boolean webArchiveTitleFilter = true;
        boolean webArchiveContentFilter = true;

        boolean enableJumingFilter = true;

        int domainFilterMode = 1;
        boolean enableBaiduDomain = true;
        boolean enableBaiduSite = true;
        boolean enableSo360Search = true;
        boolean enableSo360Site = true;
        boolean enableSogouDomain = true;
        boolean enableSogouSearch = true;

        String ip = "127.0.0.1";
        try {
            ip = System.getProperty("IP");
        } catch (Exception e) {

        }

        if (ip.equals("127.0.0.1")) {
            ip = "0.0.0.0";
        }

        InetAddress tmpIp = null;
        if (ip != null) {
            try {
                tmpIp = InetAddress.getByName(ip);
            } catch (UnknownHostException ex) {
            }
            if (tmpIp == null) {
                try {
                    tmpIp = InetAddress.getLocalHost();
                } catch (UnknownHostException ex) {
                }
            }
            System.out.println("IP設定為 " + tmpIp);
        }

        localAddress = tmpIp;

        try (InputStream input = new FileInputStream("config.txt")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            enableWebArchive = (prop.containsKey("ENABLE_WEBARCHIVE") && (readInt(prop.getProperty("ENABLE_WEBARCHIVE")) == 1));

            if (prop.containsKey("WEBARCHIVE_MODE")) {
                webArchiveMode = readInt(prop.getProperty("WEBARCHIVE_MODE"));
            }
            webArchiveTitleFilter = (prop.containsKey("WEBARCHIVE_TITLE_FILTER") && (readInt(prop.getProperty("WEBARCHIVE_TITLE_FILTER")) == 1));
            webArchiveContentFilter = (prop.containsKey("WEBARCHIVE_CONTENT_FILTER") && (readInt(prop.getProperty("WEBARCHIVE_CONTENT_FILTER")) == 1));

            enableJumingFilter = (prop.containsKey("ENABLE_JUMING_FILTER") && (readInt(prop.getProperty("ENABLE_JUMING_FILTER")) == 1));

            if (prop.containsKey("DOMAIN_FILTER_MODE")) {
                domainFilterMode = readInt(prop.getProperty("DOMAIN_FILTER_MODE"));
            }
            enableBaiduDomain = (prop.containsKey("ENABLE_BAIDU_DOMAIN") && (readInt(prop.getProperty("ENABLE_BAIDU_DOMAIN")) == 1));
            enableBaiduSite = (prop.containsKey("ENABLE_BAIDU_SITE") && (readInt(prop.getProperty("ENABLE_BAIDU_SITE")) == 1));
            enableSo360Search = (prop.containsKey("ENABLE_SO360_SEARCH") && (readInt(prop.getProperty("ENABLE_SO360_SEARCH")) == 1));
            enableSo360Site = (prop.containsKey("ENABLE_SO360_SITE") && (readInt(prop.getProperty("ENABLE_SO360_SITE")) == 1));
            enableSogouDomain = (prop.containsKey("ENABLE_SOGOU_DOMAIN") && (readInt(prop.getProperty("ENABLE_SOGOU_DOMAIN")) == 1));
            enableSogouSearch = (prop.containsKey("ENABLE_SOGOU_SEARCH") && (readInt(prop.getProperty("ENABLE_SOGOU_SEARCH")) == 1));

        } catch (IOException ex) {
            //ex.printStackTrace();
            System.out.println("未找到設定檔config.txt，載入預設值。");
            try {
                saveDefaultConfig();
            } catch (Exception ex1) {
                //Logger.getLogger(Configure.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        DOMAIN_FILTER_MODE = domainFilterMode;
        WEBARCHIVE_TITLE_FILTER = webArchiveTitleFilter;
        WEBARCHIVE_CONTENT_FILTER = webArchiveContentFilter;

        ENABLE_WEBARCHIVE = enableWebArchive;
        Filter.WEB_ARCHIVE.setEnable(enableWebArchive);

        ENABLE_JUMING_FILTER = enableJumingFilter;
        Filter.JUMING.setEnable(enableJumingFilter);

        ENABLE_BAIDU_DOMAIN = enableBaiduDomain;
        Filter.BAIDU_DOMAIN.setEnable(enableBaiduDomain);

        ENABLE_BAIDU_SITE = enableBaiduSite;
        Filter.BAIDU_SITE.setEnable(enableBaiduSite);

        ENABLE_SO360_SEARCH = enableSo360Search;
        Filter.SO360_SEARCH.setEnable(enableSo360Search);

        ENABLE_SO360_SITE = enableSo360Site;
        Filter.SO360_SITE.setEnable(enableSo360Site);

        ENABLE_SOGOU_DOMAIN = enableSogouDomain;
        Filter.SOGOU_DOMAIN.setEnable(enableSogouDomain);

        ENABLE_SOGOU_SEARCH = enableSogouSearch;
        Filter.SOGOU_SEARCH.setEnable(enableSogouSearch);
    }

    static int readInt(String s) {
        return Integer.parseInt(s.replace(" ", ""));
    }

    public static void printStatus() {
        System.out.println(ENABLE_WEBARCHIVE ? "WebArchive過濾'啟動'" : "WebArchive過濾'未啟動'");
        if (ENABLE_WEBARCHIVE) {
            System.out.println(WEBARCHIVE_TITLE_FILTER ? "WebArchive過濾Title'啟動'" : "WebArchive過濾Title'未啟動'");
            System.out.println(WEBARCHIVE_CONTENT_FILTER ? "WebArchive過濾Content'啟動'" : "WebArchive過濾Content'未啟動'");
        }

        System.out.println(ENABLE_JUMING_FILTER ? "聚名網過濾'啟動'" : "聚名網過濾'未啟動'");
        if (ENABLE_SOGOU_DOMAIN || ENABLE_BAIDU_DOMAIN) {
            System.out.println(DOMAIN_FILTER_MODE == 1 ? "域名過濾模式'嚴格'" : "域名過濾模式'寬鬆'");
        }
        System.out.println(ENABLE_BAIDU_DOMAIN ? "百度域名過濾'啟動'" : "百度域名過濾'未啟動'");
        System.out.println(ENABLE_BAIDU_SITE ? "百度網站過濾'啟動'" : "百度網站過濾'未啟動'");
        System.out.println(ENABLE_SO360_SEARCH ? "360搜尋過濾'啟動'" : "360搜尋過濾'未啟動'");
        System.out.println(ENABLE_SO360_SITE ? "360網站過濾'啟動'" : "360網站過濾'未啟動'");
        System.out.println(ENABLE_SOGOU_DOMAIN ? "搜狗域名過濾'啟動'" : "搜狗域名過濾'未啟動'");
        System.out.println(ENABLE_SOGOU_SEARCH ? "搜狗搜尋過濾'啟動'" : "搜狗搜尋過濾'未啟動'");

    }

    static void saveDefaultConfig() throws Exception {

        @Cleanup
        OutputStream file = new FileOutputStream("config.txt");
        @Cleanup
        OutputStreamWriter output = new OutputStreamWriter(file, "utf-8");
        output.write(comm);
//        Properties prop = new Properties();
//        // set the properties value
//        prop.setProperty("DOMAIN_FILTER_MODE", String.valueOf(1));
//        prop.setProperty("ENABLE_BAIDU_DOMAIN", String.valueOf(1));
//        prop.setProperty("ENABLE_BAIDU_SITE", String.valueOf(1));
//        prop.setProperty("ENABLE_SO360_SEARCH", String.valueOf(1));
//        prop.setProperty("ENABLE_SO360_SITE", String.valueOf(1));
//        prop.setProperty("ENABLE_SOGOU_DOMAIN", String.valueOf(1));
//        prop.setProperty("ENABLE_SOGOU_SEARCH", String.valueOf(1));
//        prop.setProperty("ENABLE_JUMING_FILTER", String.valueOf(1));
//        prop.setProperty("ENABLE_WEBARCHIVE", String.valueOf(1));
//        // save properties to project root folder
//        prop.store(output, null);
        System.out.println("預設值以儲存為config.txt。");
    }

    static void saveConfig() throws Exception {

        @Cleanup
        OutputStream file = new FileOutputStream("config.txt");
        @Cleanup
        OutputStreamWriter output = new OutputStreamWriter(file, "utf-8");
        output.write(comm);
//        Properties prop = new Properties();
//        // set the properties value
//        prop.setProperty("DOMAIN_FILTER_MODE", String.valueOf(DOMAIN_FILTER_MODE));
//        prop.setProperty("ENABLE_BAIDU_DOMAIN", String.valueOf(ENABLE_BAIDU_DOMAIN ? 1 : 0));
//        prop.setProperty("ENABLE_BAIDU_SITE", String.valueOf(ENABLE_BAIDU_SITE ? 1 : 0));
//        prop.setProperty("ENABLE_SO360_SEARCH", String.valueOf(ENABLE_SO360_SEARCH ? 1 : 0));
//        prop.setProperty("ENABLE_SO360_SITE", String.valueOf(ENABLE_SO360_SITE ? 1 : 0));
//        prop.setProperty("ENABLE_SOGOU_DOMAIN", String.valueOf(ENABLE_SOGOU_DOMAIN ? 1 : 0));
//        prop.setProperty("ENABLE_SOGOU_SEARCH", String.valueOf(ENABLE_SOGOU_SEARCH ? 1 : 0));
//        prop.setProperty("ENABLE_JUMING_FILTER", String.valueOf(ENABLE_JUMING_FILTER ? 1 : 0));
//        prop.setProperty("ENABLE_WEBARCHIVE", String.valueOf(ENABLE_WEBARCHIVE ? 1 : 0));

        // save properties to project root folder
        //prop.store(output, null);
    }
}
