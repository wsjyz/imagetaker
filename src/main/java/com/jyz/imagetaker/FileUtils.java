package com.jyz.imagetaker;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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

            InputStream in = new FileInputStream(filePath + "conf.properties");
            p.load(in);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return p.getProperty(key);
    }

}
