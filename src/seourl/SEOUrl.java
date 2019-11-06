/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl;

import seourl.other.Configure;
import seourl.other.Tools;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import seourl.data.UrlDataSet;
import seourl.enabler.BaiduDomainFilterEnabler;
import seourl.enabler.BaiduSiteFilterEnabler;
import seourl.enabler.JumingFilterEnabler;
import seourl.enabler.So360SearchFilterEnabler;
import seourl.enabler.So360SiteFilterEnabler;
import seourl.enabler.SogouDomainFilterEnabler;
import seourl.enabler.SogouSearchFilterEnabler;
import seourl.enabler.WebArchiveFilterEnabler;
import seourl.enabler.ex.EnablerAbstract;
import seourl.pack.ex.PackAbstract;
import seourl.template.TemplateIndex;

/**
 *
 * @author Yuri
 */
public class SEOUrl {

    @Getter
    private int test;
    /**
     * @param args the command line arguments
     */
    private static final Logger LOG = Logger.getLogger(SEOUrl.class.getName());

    private final List<EnablerAbstract> enablerAbstractList = new ArrayList<>();

    /**
     * Get the value of enablerAbstractList
     *
     * @return the value of enablerAbstractList
     */
    private final UrlDataSet urlDataSet = new UrlDataSet();

    public SEOUrl() {
        Configure.printStatus();
        checkFile();
        this.urlDataSet.setData(Tools.loadUrl());
    }

    public static void main(String[] args) {
        // TODO code application logic here

//        try {
//            System.setErr(new PrintStream(new FileOutputStream("error.log", true)));
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        }
        SEOUrl s = new SEOUrl();

//        List<String> title = Tools.loadKeyword("WEBARCHIVE-TITLE.txt");;
//        List<String> content = Tools.loadKeyword("WEBARCHIVE-CONTENT.txt");
//        WebArchiveFilter3 waf3 = new WebArchiveFilter3(0, title, content);
//        waf3.doAnalysis("1banchina.com", 20180805032156L);
        s.start();
        //s.waitTime();

    }

    private void waitTime() {
        while (true) {
            Tools.sleep(100);
        }
    }

    public void start() {
        if (Configure.ENABLE_WEBARCHIVE) {
            enablerAbstractList.add(WebArchiveFilterEnabler.getInstance());
            WebArchiveFilterEnabler.getInstance().setDsa(urlDataSet.getClone());
        }

        if (Configure.ENABLE_JUMING_FILTER) {
            enablerAbstractList.add(JumingFilterEnabler.getInstance());
            JumingFilterEnabler.getInstance().setDsa(urlDataSet.getClone());
        }

        if (Configure.ENABLE_BAIDU_DOMAIN) {
            enablerAbstractList.add(BaiduDomainFilterEnabler.getInstance());
            BaiduDomainFilterEnabler.getInstance().setDsa(urlDataSet.getClone());
        }

        if (Configure.ENABLE_BAIDU_SITE) {
            enablerAbstractList.add(BaiduSiteFilterEnabler.getInstance());
            BaiduSiteFilterEnabler.getInstance().setDsa(urlDataSet.getClone());
        }
        if (Configure.ENABLE_SO360_SEARCH) {
            enablerAbstractList.add(So360SearchFilterEnabler.getInstance());
            So360SearchFilterEnabler.getInstance().setDsa(urlDataSet.getClone());
        }
        if (Configure.ENABLE_SO360_SITE) {
            enablerAbstractList.add(So360SiteFilterEnabler.getInstance());
            So360SiteFilterEnabler.getInstance().setDsa(urlDataSet.getClone());
        }

        if (Configure.ENABLE_SOGOU_DOMAIN) {
            enablerAbstractList.add(SogouDomainFilterEnabler.getInstance());
            SogouDomainFilterEnabler.getInstance().setDsa(urlDataSet.getClone());
        }
        if (Configure.ENABLE_SOGOU_SEARCH) {
            enablerAbstractList.add(SogouSearchFilterEnabler.getInstance());
            SogouSearchFilterEnabler.getInstance().setDsa(urlDataSet.getClone());
        }
        //Tools.sleep(100000);
        for (EnablerAbstract ea : enablerAbstractList) {
            ea.start();
        }

        for (EnablerAbstract ea : enablerAbstractList) {
            try {
                ea.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        saveFile();
    }

    private void saveFile() {

        int[] count = {0, 0, 0};

        TemplateIndex passT = new TemplateIndex(Configure.startTime);
        passT.insertTime(Configure.startTime);
        passT.setSaveName("index");

        TemplateIndex failT = new TemplateIndex(Configure.startTime);
        failT.insertTime(Configure.startTime);
        failT.setSaveName("index_f");

        for (String url : urlDataSet.getListCopy()) {
            this.insertRecord(passT, failT, url, count);
        }
        passT.creatFile();
        failT.creatFile();
        System.out.printf("URL數量:%d  通過:%d 未通過:%d  unknow:%d  \r\n", urlDataSet.getSize(), count[0], count[1], count[2]);
        long total = (System.currentTimeMillis() - Configure.startTime.getTime());
        long h = TimeUnit.MILLISECONDS.toHours(total);
        long m = TimeUnit.MILLISECONDS.toMinutes(total) - (h * 60);
        long s = TimeUnit.MILLISECONDS.toSeconds(total) - ((h * 60) + m) * 60;
        System.out.printf("總執行時間:%d小時 %d分鐘 %d秒\n", h, m, s);
    }

    private void insertRecord(TemplateIndex passT, TemplateIndex failT, String url, int[] count) {

        String[] wapStr = {"", "未啟用"};
        String jgp = "未啟用";
        String[] bdpStr = {"", "未啟用"};
        String[] bspStr = {"", "未啟用"};
        String[] s3sepStr = {"", "未啟用"};
        String[] s3sipStr = {"", "未啟用"};
        String[] sdpStr = {"", "未啟用"};
        String[] sspStr = {"", "未啟用"};

        boolean isPass = true;
        PackAbstract pack;
        if (Configure.ENABLE_WEBARCHIVE) {
            pack = WebArchiveFilterEnabler.getInstance().getPackMap().get(url);
            isPass = isPass && pack.allPass();
            wapStr = pack.getIndexStr();

        }
        if (Configure.ENABLE_JUMING_FILTER) {
            pack = JumingFilterEnabler.getInstance().getPackMap().get(url);
            isPass = isPass && pack.allPass();
            jgp = pack.getIndexStr()[0];
        }

        if (Configure.ENABLE_BAIDU_DOMAIN) {
            pack = BaiduDomainFilterEnabler.getInstance().getPackMap().get(url);
            isPass = isPass && pack.allPass();
            bdpStr = pack.getIndexStr();
        }
        if (Configure.ENABLE_BAIDU_SITE) {
            pack = BaiduSiteFilterEnabler.getInstance().getPackMap().get(url);
            isPass = isPass && pack.allPass();
            bspStr = pack.getIndexStr();
        }

        if (Configure.ENABLE_SO360_SEARCH) {
            pack = So360SearchFilterEnabler.getInstance().getPackMap().get(url);
            isPass = isPass && pack.allPass();
            s3sepStr = pack.getIndexStr();
        }
        if (Configure.ENABLE_SO360_SITE) {
            pack = So360SiteFilterEnabler.getInstance().getPackMap().get(url);
            isPass = isPass && pack.allPass();
            s3sipStr = pack.getIndexStr();
        }
        if (Configure.ENABLE_SOGOU_DOMAIN) {
            pack = SogouDomainFilterEnabler.getInstance().getPackMap().get(url);
            isPass = isPass && pack.allPass();
            sdpStr = pack.getIndexStr();
        }
        if (Configure.ENABLE_SOGOU_SEARCH) {
            pack = SogouSearchFilterEnabler.getInstance().getPackMap().get(url);
            isPass = isPass && pack.allPass();
            sspStr = pack.getIndexStr();
        }

        if (!isPass) {
            failT.insertRecord(url, wapStr, jgp,
                    bdpStr,
                    bspStr,
                    s3sepStr,
                    s3sipStr,
                    sdpStr,
                    sspStr);
            count[1]++;
        } else {
            passT.insertRecord(url, wapStr, jgp,
                    bdpStr,
                    bspStr,
                    s3sepStr,
                    s3sipStr,
                    sdpStr,
                    sspStr);
            count[0]++;
        }
    }

    public void checkFile() {

        File i = new File("input.txt");

        if (!i.exists()) {
            System.out.println("未找到現有input.txt檔案，已建立input.txt");
            List<String> comm = new ArrayList<>();
            comm.add("###請在下方加域名，請誤刪除這行###");
            try {
                Files.write(Paths.get("input.txt"), comm, StandardCharsets.UTF_8);
            } catch (IOException ex) {
                Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (!i.exists()) {
            System.out.println("請設定好keywords.txt或input.txt在執行。");
            System.exit(1);
        }

    }

}
