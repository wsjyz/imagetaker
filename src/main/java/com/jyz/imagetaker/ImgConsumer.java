package com.jyz.imagetaker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by dam on 2015/1/4.
 */
public class ImgConsumer extends Thread {

    BlockingQueue<String> queue = new ArrayBlockingQueue<String>(10);

    public ImgConsumer(){

    }
    public void run(){

    }
}
