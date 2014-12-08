package com.jyz.imagetaker;

import com.jyz.imagetaker.analysisImage.AnalysisEvernote;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 上传图片线程处理
 * Created by dam on 2014/11/19.
 */
public class UploadImgTask {

    protected static Logger logger = Logger.getLogger(UploadImgTask.class);

    private static ExecutorService exec = Executors.newFixedThreadPool(10);

    public static void addToUploadPool(String filePath) {

        boolean result = true;
        String ak = AnalysisEvernote.confMap.get("ACCESS_KEY").toString().trim();
        String sk = AnalysisEvernote.confMap.get("SECRET_KEY").toString().trim();
        //获取上传账户信息
        String qnUserName = FileUtils.getPropertiesValue(
                                FileUtils.findJarPath(),"qnUserName");
        Map<String,String> userInfoMap = (Map<String,String>)AnalysisEvernote.confMap.get(qnUserName);

        String bn = userInfoMap.get("bucketName");
        String domain = userInfoMap.get("domain");
//        if (StringUtils.isBlank(ak) || StringUtils.isBlank(sk) || StringUtils.isBlank(bn) || StringUtils.isBlank(domain)) {
//            logger.info("服务端存储配置设置错误，请仔细检查（key=ACCESS_KEY、SECRET_KEY、bucketName、domain）");
//            result = false;
            ak = Constants.ACCESS_KEY;
            sk = Constants.SECRET_KEY;
            bn = Constants.BUCKET_NAME;
            domain = Constants.DOMAIN;
        //}

        UploadImgCallable callable = new UploadImgCallable(ak, sk, bn, filePath, domain);
        FutureTask<String> task = new FutureTask<String>(callable);
        if (!exec.isShutdown()) {
            exec.submit(task);
        }

    }

}
