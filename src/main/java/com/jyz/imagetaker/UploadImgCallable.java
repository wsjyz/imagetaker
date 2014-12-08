package com.jyz.imagetaker;

import com.jyz.imagetaker.analysisImage.AnalysisEvernote;
import com.jyz.imagetaker.analysisImage.ImageCompose;
import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.config.Config;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.rs.GetPolicy;
import com.qiniu.api.rs.PutPolicy;
import com.qiniu.api.rs.URLUtils;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;

import java.util.concurrent.Callable;

/**
 * 上传图片单个任务
 * Created by dam on 2014/11/19.
 */
public class UploadImgCallable implements Callable<String> {

    protected static Logger logger = Logger.getLogger(UploadImgCallable.class);

    public String accessKey;
    public String secretKey;
    public String bucketName;
    public String fileUri;
    public String domain;

    public UploadImgCallable(String ak,String sk,String bn,String fileUri,String dm){
        this.accessKey = ak;
        this.secretKey = sk;
        this.bucketName = bn;
        this.fileUri = fileUri;
        this.domain = dm;
    }
    @Override
    public String call() throws Exception {
        String result = "";
        Config.ACCESS_KEY = accessKey;
        Config.SECRET_KEY = secretKey;
        Mac mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
        //上传文件
        PutPolicy putPolicy = new PutPolicy(bucketName);
        String uptoken = null;
        try {
            uptoken = putPolicy.token(mac);
        } catch (AuthException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PutExtra extra = new PutExtra();
        String key = UUIDGen.genShortPK();
        logger.info("上传" + fileUri);
        PutRet ret = IoApi.putFile(uptoken, key, fileUri, extra);
        //获取下载地址
        if(ret.ok()){
            String baseUrl = null;
            try {
                baseUrl = URLUtils.makeBaseUrl(domain, key);
            } catch (EncoderException e) {
                e.printStackTrace();
            }
            GetPolicy getPolicy = new GetPolicy();
            String downloadUrl = null;
            try {
                downloadUrl = getPolicy.makeRequest(baseUrl, mac);
                result = downloadUrl;
            } catch (AuthException e) {
                e.printStackTrace();
            }
            logger.info("上传成功：" + downloadUrl);

            String newFilePath = FileUtils.getPropertiesValue(
                    FileUtils.findJarPath(),"NEW_FILE_PATH")
                    ;
            if(StringUtils.isBlank(newFilePath)){
                newFilePath = Constants.NEW_FILE_PATH;
                logger.error("没有设置新文件存放路径(key=newFilePath),使用默认路径"+Constants.NEW_FILE_PATH);
            }
            ImageCompose.fixImage(downloadUrl,fileUri,newFilePath);
        }else{

            result = ret.getResponse();
            logger.info("上传失败：" + result);
        }
        return result;
    }
}
