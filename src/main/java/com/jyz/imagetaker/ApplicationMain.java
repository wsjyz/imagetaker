package com.jyz.imagetaker;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;




import com.jyz.imagetaker.analysisImage.InitQiNiuUrl;

import java.io.Console;
import java.util.Map;

/**
 * 程序入口
 * Created by dam on 2014/12/1.
 */
public class ApplicationMain {

    protected Logger logger = Logger.getLogger(ApplicationMain.class);

    private static Map<String,Object> confMap = InitQiNiuUrl.getInstance().readUrl();

    public void runApp(){

        boolean result = true;
        String qnUserName = FileUtils.getPropertiesValue(
                FileUtils.findJarPath(),"qnUserName");
        if(StringUtils.isBlank(qnUserName)){
            result = false;
        }else{
            Map<String,String> userInfoMap = (Map<String,String>)InitQiNiuUrl.getInstance().confMap.get(qnUserName);
            if(userInfoMap == null || userInfoMap.isEmpty()){
                result = false;
            }
        }
        Object akObj = InitQiNiuUrl.getInstance().confMap.get("ACCESS_KEY");
        Object skObj = InitQiNiuUrl.getInstance().confMap.get("SECRET_KEY");
        if(akObj == null||skObj == null){
            result = false;
        }
        String imagePath = FileUtils.getPropertiesValue(
                FileUtils.findJarPath(),"UPLOAD_IMAGE_PATH");

        if(imagePath == null){
            logger.info("没有设置相片上传目录(key=uploadImagePath),使用"+Constants.UPLOAD_IMAGE_PATH);
            imagePath = Constants.UPLOAD_IMAGE_PATH;
        }

        if(result){

            PathWorker worker = new PathWorker();
            worker.uploadOldImg(imagePath);
            worker.watchPath(imagePath);
            worker.findUploadImg();
        }else{
            logger.info("用户名不存在，请退出程序，并联系客服解决!");

        }

    }

    public static void main(String[] args) {
        Console console = System.console();
        if(console != null){
            ApplicationMain main = new ApplicationMain();
            main.runApp();
            console.readLine();
        }


    }
}
