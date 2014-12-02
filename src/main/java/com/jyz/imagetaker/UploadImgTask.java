package com.jyz.imagetaker;

import com.jyz.imagetaker.analysisImage.AnalysisEvernote;
import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.config.Config;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.rs.GetPolicy;
import com.qiniu.api.rs.PutPolicy;
import com.qiniu.api.rs.URLUtils;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;

import java.util.List;
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
        String ak = AnalysisEvernote.confMap.get("ACCESS_KEY");
        String sk = AnalysisEvernote.confMap.get("SECRET_KEY");
        String bn = AnalysisEvernote.confMap.get("bucketName");
        String domain = AnalysisEvernote.confMap.get("domain");
        if (StringUtils.isBlank(ak) || StringUtils.isBlank(sk) || StringUtils.isBlank(bn) || StringUtils.isBlank(domain)) {
            logger.info("服务端存储配置设置错误，请仔细检查（key=ACCESS_KEY、SECRET_KEY、bucketName、domain）");
            result = false;
            ak = Constants.ACCESS_KEY;
            sk = Constants.SECRET_KEY;
            bn = Constants.BUCKET_NAME;
            domain = Constants.DOMAIN;
        }

        UploadImgCallable callable = new UploadImgCallable(ak, sk, bn, filePath, domain);
        FutureTask<String> task = new FutureTask<String>(callable);
        if (!exec.isShutdown()) {
            exec.submit(task);
        }

    }

}
