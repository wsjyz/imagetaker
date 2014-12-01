package com.eighth.airrent.test;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Test {
	public static void main(String args[]) {
		try {
			// 读取第一张图片
			File fileOne = new File("E:/yz/image/1.jpg");
			BufferedImage ImageOne = ImageIO.read(fileOne);
			int width = ImageOne.getWidth();// 图片宽度
			int height = ImageOne.getHeight();// 图片高度
			// 从图片中读取RGB
			int[] ImageArrayOne = new int[width * height];
			ImageArrayOne = ImageOne.getRGB(0, 0, width, height, ImageArrayOne,
					0, width);
			// 对第二张图片做相同的处理
			File fileTwo = new File("E:/yz/image/2.jpg");
			BufferedImage ImageTwo = ImageIO.read(fileTwo);
			int[] ImageArrayTwo = new int[50 * 50];
			ImageArrayTwo = ImageTwo.getRGB(0, 0, 50, 50, ImageArrayTwo,
					0, 50);
			// 生成新图片
			BufferedImage ImageNew = new BufferedImage(width,height,
					BufferedImage.TYPE_INT_RGB);
			ImageNew.setRGB(0, 0, width, height, ImageArrayOne, 0, width);// 设置左半部分的RGB
			ImageNew.setRGB(width-50, height-50, 50, 50, ImageArrayTwo, 0, 50);// 设置右半部分的RGB
			File outFile = new File("E:/yz/image/3.jpg");
			ImageIO.write(ImageNew, "png", outFile);// 写图片
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
