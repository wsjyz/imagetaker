package com.jyz.imagetaker;


import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 目录监控
 * Created by dam on 2014/11/19.
 */
public class PathWorker {

    /**
     * 监控某种新增的文件
     * @param imgPath
     * @return
     */
    public List<String> findUploadImg(String imgPath){
        List<String> fileList = new ArrayList<String>();
        WatchService service = null;
        try {
            service = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(imgPath);
            path.register(service, StandardWatchEventKinds.ENTRY_CREATE);
            while (true){
                WatchKey key = service.take();
                for(WatchEvent<?> event : key.pollEvents()){
                    Path createdPath = (Path)event.context();
                    createdPath = path.resolve(createdPath);
                    long size = Files.size(createdPath);
                    System.out.println(createdPath + " "+size);
                    UploadImgTask.addToUploadPool(createdPath.toString());
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return fileList;
    }

    public static void main(String[] args) {
        PathWorker worker = new PathWorker();
        List<String> fileList = worker.findUploadImg("D:\\soft");
    }
}
