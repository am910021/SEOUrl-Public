/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 *
 * @author yuri
 */
public class Tools {

    static Random ran = new Random();

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
    
}
