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
import com.jyz.imagetaker.FileUtils;
import org.apache.log4j.Logger;

public class ImageCompose {

    protected static Logger logger = Logger.getLogger(ImageCompose.class);

    public static String fixImage(String downloadUrl,String srcImgPath,String descImgPath){
        String result = "";
        String tdFilePath = getImageGenerate(downloadUrl,srcImgPath);
        getImageCompose(srcImgPath,tdFilePath,descImgPath);
        return result;
    }
	/**
	 * 
	 * @param downloadPath 下载地址
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getImageGenerate(String downloadPath,String imagePath){

        String tdFilePath = "";
		try {
			//解析成二维码图片
			MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
			Map hints = new HashMap();
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			hints.put(EncodeHintType.MARGIN, 1);
			BitMatrix bitMatrix = multiFormatWriter.encode(downloadPath,
					BarcodeFormat.QR_CODE, 50, 50, hints);
            String imgSuffix = "";
            if(imagePath.contains(".")){
                imgSuffix = imagePath.substring(imagePath.lastIndexOf(".") + 1);
            }
            File imageFile = new File(imagePath);
            String imageName = "";
            if(imageFile.exists()){
                imageName = imageFile.getName().substring(0,imageFile.getName().lastIndexOf("."));
            }
            File tdFileDirectory = new File(imageFile.getParent()+"\\td");
            if(!tdFileDirectory.exists()){
                tdFileDirectory.mkdirs();
            }
			File tdFile = new File(imageFile.getParent()+"\\td", imageName+"_td."+imgSuffix);

			MatrixToImageWriter.writeToFile(bitMatrix, imgSuffix, tdFile);

            tdFilePath = tdFile.getPath();
        } catch (Exception e) {
			e.printStackTrace();
		}
        return tdFilePath;
	}
	/**
	 * 合成2张图片成为新图片
	 * @param srcImagePath 主图片路径
	 * @param tdFilePath 二维码图片路径
	 * @param newFilePath 生成图片路径
	 */
	public static void getImageCompose(String srcImagePath,String tdFilePath,String newFilePath){
		try {
			// 读取第一张图
			File fileOne = new File(srcImagePath);
			BufferedImage ImageOne = ImageIO.read(fileOne);
			int width = ImageOne.getWidth();// 图片宽度
			int height = ImageOne.getHeight();// 图片高度
			// 从图片中读取RGB
			int[] ImageArrayOne = new int[width * height];
			ImageArrayOne = ImageOne.getRGB(0, 0, width, height, ImageArrayOne,
					0, width);
			// 对第二张图片做相同的处理
			File fileTwo = new File(tdFilePath);
			BufferedImage ImageTwo = ImageIO.read(fileTwo);
			int[] ImageArrayTwo = new int[50 * 50];
			ImageArrayTwo = ImageTwo.getRGB(0, 0, 50, 50, ImageArrayTwo,
					0, 50);
			// 生成新图
			BufferedImage ImageNew = new BufferedImage(width,height,
					BufferedImage.TYPE_INT_RGB);
			ImageNew.setRGB(0, 0, width, height, ImageArrayOne, 0, width);// 设置左半部分的RGB
			ImageNew.setRGB(width-50, height-50, 50, 50, ImageArrayTwo, 0, 50);// 设置右半部分的RGB

            File newFileDirectory = new File(newFilePath);
            if(!newFileDirectory.exists()){
                newFileDirectory.mkdirs();
            }
            //下面的代码是创建父级目录
            String imagePath = FileUtils.getPropertiesValue(
                    FileUtils.findJarPath(), "UPLOAD_IMAGE_PATH");
            String newImagePath = newFilePath +"\\" + srcImagePath.substring(
                    srcImagePath.indexOf(imagePath)+imagePath.length(),srcImagePath.length());
            File newImageDir = new File(newImagePath.substring(0,newImagePath.lastIndexOf(File.separator)));
            if(!newImageDir.exists()){
                newImageDir.mkdirs();
            }


            File outFile = new File(newImagePath);

            String imgSuffix = "";
            if(srcImagePath.contains(".")){
                imgSuffix = srcImagePath.substring(srcImagePath.lastIndexOf(".") + 1);
            }

			ImageIO.write(ImageNew, imgSuffix, outFile);// 写图

            //删除二维码
            fileTwo.delete();
            logger.info(srcImagePath+" 二维码生成完毕");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
