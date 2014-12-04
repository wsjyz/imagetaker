package com.jyz.imagetaker;

import com.jyz.imagetaker.analysisImage.AnalysisEvernote;
import org.apache.log4j.Logger;


import java.util.Map;
import java.util.Properties;

/**
 * 程序入口
 * Created by dam on 2014/12/1.
 */
public class ApplicationMain {

    protected Logger logger = Logger.getLogger(ApplicationMain.class);

    private static Map<String,String> confMap = AnalysisEvernote.getInstance().listNotes();

    public void runApp(){

        String imagePath = FileUtils.getPropertiesValue(
                 FileUtils.findJarPath(),"uploadImagePath");

        if(imagePath == null){
            logger.info("没有设置相片上传目录(key=uploadImagePath),使用"+Constants.UPLOAD_IMAGE_PATH);
            imagePath = Constants.UPLOAD_IMAGE_PATH;
        }
        PathWorker worker = new PathWorker();
        worker.findUploadImg(imagePath);
    }

    public static void main(String[] args) {

        ApplicationMain main = new ApplicationMain();
        main.runApp();

    }
}
