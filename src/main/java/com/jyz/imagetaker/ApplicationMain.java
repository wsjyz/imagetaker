package com.jyz.imagetaker;


import org.apache.log4j.Logger;




import com.jyz.imagetaker.analysisImage.InitQiNiuUrl;

import java.util.Map;

/**
 * 程序入口
 * Created by dam on 2014/12/1.
 */
public class ApplicationMain {

    protected Logger logger = Logger.getLogger(ApplicationMain.class);

    private static Map<String,Object> confMap = InitQiNiuUrl.getInstance().readUrl();

    public void runApp(){

        String imagePath = FileUtils.getPropertiesValue(
                 FileUtils.findJarPath(),"UPLOAD_IMAGE_PATH");

        if(imagePath == null){
            logger.info("没有设置相片上传目录(key=uploadImagePath),使用"+Constants.UPLOAD_IMAGE_PATH);
            imagePath = Constants.UPLOAD_IMAGE_PATH;
        }

        PathWorker worker = new PathWorker();
        worker.uploadOldImg(imagePath);
        worker.watchPath(imagePath);
        worker.findUploadImg();
    }

    public static void main(String[] args) {
        ApplicationMain main = new ApplicationMain();
        main.runApp();

    }
}
