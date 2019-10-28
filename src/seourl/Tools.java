/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author yuri
 */
public class Tools {

    private static Random ran = new Random();

    public static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            return ran.nextInt(5) + 1;
        }
        return ran.nextInt((max - min) + 1) + min;
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
        }
    }

    public static void sleep(int min, int max) {
        try {
            Thread.sleep(getRandomNumberInRange(min, max));
        } catch (Exception e) {
        }
    }

    public static boolean checkDir(String path) {
        try {
            File f = new File(path);
            if (f.exists()) {
                return true;
            }
            f.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     *
     * @param long mi
     *
     * @return yyyy/MM/dd HH:mm:ss.SSS
     */
    public static String getFormatDate1(long mi) {
        return getFormatDate1(new Date(mi));
    }

    /**
     *
     * @param Date date
     *
     * @return yyyy/MM/dd HH:mm:ss.SSS
     */
    public static String getFormatDate1(Date date) {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        return sdFormat.format(date);
    }

    /**
     *
     * @param long date
     *
     * @return yyyy-MM-dd-HH-mm-ss
     */
    public static String getFormatDate2(long mi) {
        return getFormatDate2(new Date(mi));
    }

    /**
     *
     * @param Date date
     *
     * @return yyyy-MM-dd-HH-mm-ss
     */
    public static String getFormatDate2(Date date) {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return sdFormat.format(date);
    }

    public static List<String> loadKeyword(final File file) {
        List<String> keywords = new ArrayList<String>();
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String st;
            while ((st = br.readLine()) != null) {
                st.replace(" ", "").replace("\n", "");
                if (st.equals("") || st.contains("###")) {
                    continue;
                }
                keywords.add(st.toUpperCase());
            }
        } catch (IOException ex) {
        }
        return keywords;
    }

    public static List<String> loadFile() {
        List<String> list = new ArrayList<String>();
        FileFilter filter = new FileNameExtensionFilter("Text File", "txt");

        JFileChooser fileChooser = new JFileChooser();//宣告filechooser 
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(null);//叫出filechooser 
        if (returnValue == JFileChooser.APPROVE_OPTION) //判斷是否選擇檔案 
        {
            File selectedFile = fileChooser.getSelectedFile();//指派給File 
            list = Tools.loadKeyword(selectedFile);
        }

        return list;
    }

    public static String getJSON(String url, int timeout) {
        try {
            URL u = new URL(url);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.setRequestProperty("Accept-Charset", "utf-8");
            c.setRequestProperty("contentType", "utf-8");
            c.setRequestProperty("User-Agent", Device.getById(Tools.getRandomNumberInRange(0, 7)).getType());
            c.connect();
            int status = c.getResponseCode();
            //System.out.println(status);
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "utf-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
            }
        } catch (Exception ex) {
            // Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";

    }

    public static List<String> loadKeyword(String fileName) {
        List<String> keywords = new ArrayList<String>();
        try {
            File file = new File(Configure.KEY_WORD_PATH + fileName);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String st;
            while ((st = br.readLine()) != null) {
                st.replace(" ", "").replace("\n", "");

                if (st.equals("") || st.contains("###")) {
                    continue;
                }

                keywords.add(st.toUpperCase());
            }
        } catch (IOException ex) {
            //Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return keywords;
    }

    public static Document getConnect(String url, int timeout) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).timeout(timeout)
                    .userAgent(Device.getById(Tools.getRandomNumberInRange(0, 7)).getType())
                    .followRedirects(true)
                    .get();
        } catch (Exception ex) {
            //Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

        return doc;
    }

}
