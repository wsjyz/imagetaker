package com.jyz.imagetaker;

import java.io.*;
import java.util.Properties;

/**
 * Created by dam on 2014/12/4.
 */
public class FileUtils {

    /**
     * 获取当前jar包路径
     *
     * @return
     */
    public static String findJarPath() {
        String path = FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.substring(1, path.length());
        int endIndex = path.lastIndexOf("/");
        path = path.substring(0, endIndex + 1);
        try {
            path = java.net.URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }

    public static String getPropertiesValue(String filePath, String key) {

        Properties p = new Properties();
        try {

            StringReader in = new StringReader(readFileToString(filePath + "conf.properties"));

            p.load(in);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return p.getProperty(key);
    }

    /**
     * 之所以有这个方法是因为install4j这个软件的bug，获取Directory chooser路径为反斜杠
     * java.util.Properties一读取就报错
     * @param filePath
     * @return
     */
    public static String readFileToString(String filePath){
        StringBuilder sb = new StringBuilder("");

        FileReader fr = null;
        try {
            fr = new FileReader(filePath);

            BufferedReader br = new BufferedReader (fr);

            String s = "";

            while ((s = br.readLine() )!=null) {
                s = s.replaceAll("\\\\", "//");
                sb.append(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

}
