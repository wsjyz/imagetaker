package com.jyz.imagetaker;

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
import org.json.JSONException;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by dam on 2014/11/19.
 */
public class UploadImgTask {

    private static ExecutorService exec = Executors.newFixedThreadPool(10);

    public static void addToUploadPool(String filePath){

        UploadImgCallable callable = new UploadImgCallable(Constants.ACCESS_KEY,Constants.SECRET_KEY,"akdd",filePath,"akdd.qiniudn.com");
        FutureTask<String> task = new FutureTask<String>(callable);
        if(!exec.isShutdown()){
            exec.submit(task);
        }

    }

    public static void main(String[] args) {
        UploadImgTask uploadImgTask = new UploadImgTask();

    }


}
