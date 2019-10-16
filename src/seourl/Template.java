/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Yuri
 */
public class Template {

    @Getter
    String type;
    private Map<String, Pair<String, String>> tVars = new HashMap<>();
    private Map<String, List<String>> records = new HashMap<>();
    private Date startTime;

    private List<String> template = new ArrayList<>();
    private Map<Integer, String> log = new HashMap<>();

    @Setter
    private String savePath;
    @Setter
    private String saveName;

    public Template(String type, Date startTime) {
        this.startTime = startTime;
        this.type = type;
        loadTemplate();
    }

    private void loadTemplate() {
        BufferedReader br = null;
        int count = 0;
        try {
            File file = new File(String.format("template/%s.html", type));
            br = new BufferedReader(new FileReader(file));
            String st;
            String key;
            while ((st = br.readLine()) != null) {
                template.add(st);
                if (st.indexOf("key") >= 0) {
                    key = getType(st);
                    log.put(count, key);
                }
                count++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Template.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Template.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(Template.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String getType(String st) {
        String tmp = st.substring(st.indexOf("{%"), st.indexOf("%}") + 2);
        String tmp2 = tmp.replace("{% ", "").replace(" %}", "");
        int index = tmp2.indexOf("content=");
        String tKey = tmp2.substring(tmp2.indexOf("key=") + 4, index - 1);
        String tContent = tmp2.substring(index + 8, tmp2.length());
        Pair<String, String> pair = new Pair<>(tmp, tContent);
        tVars.put(tKey, pair);
        records.put(tKey, new ArrayList<String>());

        return tKey;
    }

    void insertByKey(String key, Object... args) {
        if (!tVars.containsKey(key)) {
            return;
        }

        String f = tVars.get(key).getValue();
        if (key.endsWith("hasRecord") || key.endsWith("nonRecord")) {
            records.get("Records").add(String.format(f, args));
        } else {
            records.get(key).add(String.format(f, args));
        }
    }

    void creatFile() {
        SimpleDateFormat sdFormat2 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String path0 = "output2/" + sdFormat2.format(startTime) + "/";
        if (savePath != null) {
            path0 += savePath + "/";
        }

        checkDir(path0);

        try {
            Path source = Paths.get(String.format("template/%s.html", type));
            Path dist = Paths.get(path0 + (saveName != null ? saveName : type) + ".html");
            Files.copy(source.toAbsolutePath(), dist.toAbsolutePath());
            List<String> output = new ArrayList<>();
            List<String> tmp;
            for (int i = 0; i < template.size(); i++) {
                if (log.containsKey(i)) {
                    tmp = records.get(log.get(i));
                    if (tmp.size() == 0) {
                        continue;
                    }
                    for (String s : tmp) {
                        output.add(s);
                    }
                } else {
                    output.add(template.get(i));
                }
            }
            Files.write(dist, output);
        } catch (Exception ex) {
            Logger.getLogger(Template.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean checkDir(String path) {
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
}
