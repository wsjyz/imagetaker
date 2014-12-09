package com.jyz.imagetaker;


import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * 目录监控
 * Created by dam on 2014/11/19.
 */
public class PathWorker {

    protected Logger logger = Logger.getLogger(PathWorker.class);
    /**
     * 监控某种新增的文件
     * @param imgPath
     * @return
     */
    public void findUploadImg(String imgPath){
        WatchService service = null;
        try {

            File imgPathFile = new File(imgPath);
            if(!imgPathFile.exists()){
                logger.info("创建"+imgPath);
                imgPathFile.mkdirs();
            }
            logger.info("监控"+imgPath);
            service = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(imgPath);
            path.register(service, StandardWatchEventKinds.ENTRY_CREATE);
            //设置只监控图片
            PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("regex:([^\\s]+(\\.(?i)(png|PNG|jpg|JPG))$)");
            while (true){
                WatchKey key = service.take();
                for(WatchEvent<?> event : key.pollEvents()){

                    Path createdPath = (Path)event.context();
                    if(pathMatcher.matches(createdPath.getFileName())){
                        createdPath = path.resolve(createdPath);
                        long size = Files.size(createdPath);
                        UploadImgTask.addToUploadPool(createdPath.toString());
                    }

                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void uploadOldImg(String imgPath){

        String newFilePath = FileUtils.getPropertiesValue(
                FileUtils.findJarPath(),"NEW_FILE_PATH");
        final List<String> tdFileList =  FileUtils.listDirectoryFiles(newFilePath);
        try {
            Files.walkFileTree(Paths.get(imgPath), new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String imageName = file.getFileName().toString();
                    if(!tdFileList.contains(imageName)){
                        logger.info("上传之前的图片"+file.toString());
                        UploadImgTask.addToUploadPool(file.toString());
                    }
                    return super.visitFile(file, attrs);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
