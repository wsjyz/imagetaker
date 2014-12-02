package com.jyz.imagetaker.analysisImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class ImageCompose {
	/**
	 * 
	 * @param filePath 下载地址
	 * @param fileVersion 存放路径
	 * @param imageName 图片名称
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void getImageGenerate(String filePath,String fileVersion,String imageName){
		try {
			//解析成二维码图片
			MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
			Map hints = new HashMap();
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			hints.put(EncodeHintType.MARGIN, 1);
			BitMatrix bitMatrix = multiFormatWriter.encode(filePath,
					BarcodeFormat.QR_CODE, 50, 50, hints);
			File file1 = new File(fileVersion, imageName+".jpg");
			MatrixToImageWriter.writeToFile(bitMatrix, "jpg", file1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 合成2张图片成为新图片
	 * @param fileVersion1
	 * @param fileVersion2
	 * @param fileVersion3
	 */
	public static void getImageCompose(String fileVersion1,String fileVersion2,String fileVersion3){
		try {
			// 读取第一张图
			File fileOne = new File(fileVersion1);
			BufferedImage ImageOne = ImageIO.read(fileOne);
			int width = ImageOne.getWidth();// 图片宽度
			int height = ImageOne.getHeight();// 图片高度
			// 从图片中读取RGB
			int[] ImageArrayOne = new int[width * height];
			ImageArrayOne = ImageOne.getRGB(0, 0, width, height, ImageArrayOne,
					0, width);
			// 对第二张图片做相同的处理
			File fileTwo = new File(fileVersion2);
			BufferedImage ImageTwo = ImageIO.read(fileTwo);
			int[] ImageArrayTwo = new int[50 * 50];
			ImageArrayTwo = ImageTwo.getRGB(0, 0, 50, 50, ImageArrayTwo,
					0, 50);
			// 生成新图
			BufferedImage ImageNew = new BufferedImage(width,height,
					BufferedImage.TYPE_INT_RGB);
			ImageNew.setRGB(0, 0, width, height, ImageArrayOne, 0, width);// 设置左半部分的RGB
			ImageNew.setRGB(width-50, height-50, 50, 50, ImageArrayTwo, 0, 50);// 设置右半部分的RGB
			File outFile = new File(fileVersion3);
			ImageIO.write(ImageNew, "jpg", outFile);// 写图
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		System.out.println("1");
	}
}
