package com.eighth.airrent.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class testImage {
	public static void main(String[] args) {
		try {
			//解析成二维码图片
			String content = "baidu.com";
			String path = "E:/yz/image";
			MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
			Map hints = new HashMap();
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			hints.put(EncodeHintType.MARGIN, 1);
			BitMatrix bitMatrix = multiFormatWriter.encode(content,
					BarcodeFormat.QR_CODE, 50, 50, hints);
			File file1 = new File(path, "2.jpg");
			MatrixToImageWriter.writeToFile(bitMatrix, "jpg", file1);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
