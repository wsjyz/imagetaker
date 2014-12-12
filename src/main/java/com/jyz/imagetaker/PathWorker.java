package com.jyz.imagetaker;


import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 目录监控
 * Created by dam on 2014/11/19.
 */
public class PathWorker {

    protected Logger logger = Logger.getLogger(PathWorker.class);

    //设置只监控图片
    private PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("regex:([^\\s]+(\\.(?i)(png|PNG|jpg|JPG))$)");

    private WatchService service = null;

    private final Map<WatchKey, Path> directories = new HashMap<>();

    public void watchPath(String dirName){

        File imgPathFile = new File(dirName);
        if(!imgPathFile.exists()){
            logger.info("创建"+dirName);
            imgPathFile.mkdirs();
        }

        try {
            service = FileSystems.getDefault().newWatchService();
            Files.walkFileTree(Paths.get(dirName),new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

                    if(!dir.getFileName().toString().equals("td")){
                        logger.info("监控"+dir);
                        WatchKey key = dir.register(service, StandardWatchEventKinds.ENTRY_CREATE);
                        directories.put(key,dir);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 监控某种新增的文件
     * @return
     */
    public void findUploadImg(){
        try {

            while (true){

                WatchKey key = service.take();
                for(WatchEvent<?> event : key.pollEvents()){

                    Path fileName = (Path)event.context();
                    if(pathMatcher.matches(fileName.getFileName())){
                        Path fileDir = directories.get(key);
                        Path imgPath = fileDir.resolve(fileName);
                        UploadImgTask.addToUploadPool(imgPath.toString());

                    }
                }

                key.reset();

            }
        } catch ( InterruptedException e) {
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
                    String parentDir = file.getParent().getFileName().toString();
                    if(!parentDir.equals("td")){
                        String imageName = file.getFileName().toString();
                        if(pathMatcher.matches(file.getFileName())){
                            if(!tdFileList.contains(imageName)){
                                logger.info("上传之前的图片"+file.toString());
                                UploadImgTask.addToUploadPool(file.toString());
                            }
                        }
                    }


                    return super.visitFile(file, attrs);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
