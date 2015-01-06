package com.jyz.imagetaker;

import org.apache.commons.io.filefilter.*;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by dam on 2015/1/6.
 */
public class CommonPathWorker {

    protected Logger logger = Logger.getLogger(CommonPathWorker.class);

    public void watchPath(String dirName){
        logger.info("监控"+dirName);
        // 轮询间隔 5 秒
        long interval = TimeUnit.SECONDS.toMillis(5);
        // 创建一个文件观察器用于处理文件的格式

        // Create a FileFilter

        IOFileFilter directories = FileFilterUtils.and(
                FileFilterUtils.directoryFileFilter(),
                FileFilterUtils.notFileFilter(new NameFileFilter("td")),
                HiddenFileFilter.VISIBLE);

        IOFileFilter jpgFiles = FileFilterUtils.and(
                FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter(".jpg"),
                FileFilterUtils.notFileFilter(new WildcardFileFilter("*_small*")));

        IOFileFilter JPGiles = FileFilterUtils.and(
                FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter(".JPG"),
                FileFilterUtils.notFileFilter(new WildcardFileFilter("*_small*")));

        IOFileFilter filter = FileFilterUtils.or(directories, jpgFiles,JPGiles);

        // Create the File system observer and register File Listeners
        FileAlterationObserver observer = new FileAlterationObserver(
                dirName, filter, null);
        observer.addListener(new FileMonitorFileListener()); //设置文件变化监听器
        //创建文件变化监听器
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observer);
        // 开始监控
        try {
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void uploadOldImg(String imgPath) {

        String newFilePath = FileUtils.getPropertiesValue(
                FileUtils.findJarPath(), "NEW_FILE_PATH");
        final List<String> tdFileList = FileUtils.listDirectoryFiles(newFilePath);

        List<File> files = (List<File>)org.apache.commons.io.FileUtils.listFiles(new File(imgPath),
                TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for(File file : files){
            String imageName = file.getName();
            if(!tdFileList.contains(imageName)){
                logger.info("上传之前的图片"+file.toString());
                UploadImgTask.addToUploadPool(file.toString());
            }
        }
    }
    private static class FileMonitorFileListener extends FileAlterationListenerAdaptor {
        /**
         * 文件创建执行
         */
        @Override
        public void onFileCreate(File file) {
            System.out.println("[探测到]:" + file.getAbsolutePath());

            UploadImgTask.addToUploadPool(file.getAbsolutePath());
        }

        /**
         * 文件创建修改
         */
        @Override
        public void onFileChange(File file) {
            System.out.println("[修改]:" + file.getAbsolutePath());
        }


        /**
         * 文件删除
         */
        @Override
        public void onFileDelete(File file) {
            System.out.println("[删除]:" + file.getAbsolutePath());
        }
    }


}
