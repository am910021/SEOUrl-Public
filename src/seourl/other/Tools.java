/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.other;

import seourl.type.Device;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import lombok.Cleanup;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author yuri
 */
public class Tools {

    private static Random ran = new Random();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private static final String FILE_PATH = "logs/" + sdf.format(Calendar.getInstance().getTime()) + "/";// + sdf.format(Calendar.getInstance().getTime()) + "/"
    private static final String ERROR = "error/";

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

    public static String getJSON(String url, int timeout) {
        HttpConnect hc = new HttpConnect(url, timeout, true);
        return hc.getString();

    }

    //static CloseableHttpClient httpclient = HttpClients.createDefault();
    public static Document getUrlDocument(String url, int timeout) {
        HttpConnect hc = new HttpConnect(url, timeout);
        return hc.getDocument();
    }

    public static List<String> loadUrl() {
        List<String> urls = new ArrayList<String>();
        try {
            File file = new File("input.txt");
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String st;
            while ((st = br.readLine()) != null) {
                if (st.equals("") || st.contains("###")) {
                    continue;
                }

                urls.add(st);
            }
        } catch (IOException ex) {
            //Logger.getLogger(SEOUrl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return urls;
    }

    /**
     * 輸出例外(exception)到 error資料夾
     *
     * @param fileName
     * @param t(exception)
     */
    public static void printError(final String fileName, final Throwable t) {
        if (Configure.DEBUG) {
            t.printStackTrace();
        }

        FileOutputStream out = null;
        final String file = FILE_PATH + ERROR + fileName + ".log";
        try {
            File outputFile = new File(file);
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file, true);
            out.write("---------------------------------\r\n".getBytes());
            out.write(getString(t).getBytes());
            out.write("\n---------------------------------\r\n".getBytes());
        } catch (IOException ess) {
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

    private static String getString(final Throwable e) {
        String retValue = null;
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            retValue = sw.toString();
        } finally {
            try {
                if (pw != null) {
                    pw.close();
                }
                if (sw != null) {
                    sw.close();
                }
            } catch (IOException ignore) {
            }
        }
        return retValue;
    }

    public static void checkKeyWordFile(String file) {
        Tools.checkDir(Configure.KEY_WORD_PATH);
        File k = new File(Configure.KEY_WORD_PATH + file);

        if (!k.exists()) {
            System.out.println("未找到現有" + Configure.KEY_WORD_PATH + file + "檔案，已建立" + Configure.KEY_WORD_PATH + file);
            List<String> comm = new ArrayList<>();
            comm.add("###請在下方加入關鍵詞，請誤刪除這行###");

            try {
                Files.write(Paths.get(Configure.KEY_WORD_PATH + file), comm, StandardCharsets.UTF_8);
            } catch (IOException ex) {
                Tools.printError(Tools.class.getName(), ex);
            }
        }

        if (!k.exists()) {
            System.out.println("請設定好keyword資料夾設定好關鍵詞在執行。");
            System.exit(1);
        }
    }

}
