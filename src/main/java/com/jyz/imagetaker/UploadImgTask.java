package com.jyz.imagetaker;



import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jyz.imagetaker.analysisImage.InitQiNiuUrl;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 上传图片线程处理
 * Created by dam on 2014/11/19.
 */
public class UploadImgTask {

    protected static Logger logger = Logger.getLogger(UploadImgTask.class);

    //private static ExecutorService exec = Executors.newFixedThreadPool(5);

    public static void addToUploadPool(String filePath) {

        boolean result = true;

        String ak = "";
        Object akObj = InitQiNiuUrl.getInstance().confMap.get("ACCESS_KEY");
        if(akObj == null){
            ak = Constants.ACCESS_KEY;
        }else{
            ak = InitQiNiuUrl.getInstance().confMap.get("ACCESS_KEY").toString();
        }
        String sk = "";
        Object skObj = InitQiNiuUrl.getInstance().confMap.get("SECRET_KEY");
        if( skObj == null){
            sk = Constants.SECRET_KEY;
        }else{
            sk = InitQiNiuUrl.getInstance().confMap.get("SECRET_KEY").toString();
        }
        //获取上传账户信息
        String qnUserName = FileUtils.getPropertiesValue(
                                FileUtils.findJarPath(),"qnUserName");
        Map<String,String> userInfoMap = (Map<String,String>)InitQiNiuUrl.getInstance().confMap.get(qnUserName);
        String bn = "";
        String domain = "";
        if(userInfoMap == null || userInfoMap.isEmpty()){
            result = false;
        }else{
            bn = userInfoMap.get("bucketName");
            domain = userInfoMap.get("domain");
        }


        if (StringUtils.isBlank(ak) || StringUtils.isBlank(sk) || StringUtils.isBlank(bn) || StringUtils.isBlank(domain)) {
            result = false;
        }
        if(result == false){
            logger.info("用户名不存在，请退出程序，并联系客服解决!");
            ak = Constants.ACCESS_KEY;
            sk = Constants.SECRET_KEY;
            bn = Constants.BUCKET_NAME;
            domain = Constants.DOMAIN;
        }

        UploadImgCallable callable = new UploadImgCallable(ak, sk, bn, filePath, domain);
        callable.call();
//        FutureTask<String> task = new FutureTask<String>(callable);
//        if (!exec.isShutdown()) {
//            exec.submit(task);
//        }

    }

}
