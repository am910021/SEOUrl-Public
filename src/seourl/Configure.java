/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;
import lombok.Cleanup;

/**
 *
 * @author yuri
 */
public class Configure {

    private static String comm = "#DOMAIN_FILTER_MODE  1=嚴格  2=寬鬆 \n";

    public static final int DOMAIN_FILTER_MODE;
    public static final int RELOAD_PAGE_TIME = 10; //網頁出錯的話，在10秒內重復讀

    static {
        int domainFilterMode = 1;
        try (InputStream input = new FileInputStream("config.txt")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            if (prop.containsKey("DOMAIN_FILTER_MODE")) {
                domainFilterMode = Integer.parseInt(prop.getProperty("DOMAIN_FILTER_MODE"));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        DOMAIN_FILTER_MODE = domainFilterMode;

    }

    static void saveConfig() throws Exception {

            @Cleanup
            OutputStream file = new FileOutputStream("config.txt");
            @Cleanup
            OutputStreamWriter output = new OutputStreamWriter(file, "utf-8");
            output.write(comm);
            Properties prop = new Properties();
            // set the properties value
            prop.setProperty("DOMAIN_FILTER_MODE", String.valueOf(DOMAIN_FILTER_MODE));
            // save properties to project root folder
            prop.store(output, null);

    }
}
