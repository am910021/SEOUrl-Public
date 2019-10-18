/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yuri
 */
public class TestFile {

    public TestFile() {
    }

    public void test() {
        try {
            InputStream is = getClass().getResourceAsStream("/template/index.html");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String st;
            while ((st = br.readLine()) != null) {
                System.out.println(st);
            }
        } catch (IOException ex) {
            Logger.getLogger(TestFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
