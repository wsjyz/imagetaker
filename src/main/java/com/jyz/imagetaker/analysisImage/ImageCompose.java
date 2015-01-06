package com.jyz.imagetaker.analysisImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.jyz.imagetaker.FileUtils;


import org.apache.log4j.Logger;

public class ImageCompose {

    protected static Logger logger = Logger.getLogger(ImageCompose.class);
    protected static int newImageWidth=0;
    public static String fixImage(String downloadUrl,String srcImgPath,String descImgPath){
        String result = "";
		try {
			File fileOne = new File(srcImgPath);
			BufferedImage ImageOne = ImageIO.read(fileOne);
			int width = ImageOne.getWidth();// 图片宽度
			int height = ImageOne.getHeight();// 图片高度
			if (height>width) {
				newImageWidth=width/10;
			}else{
				newImageWidth=height/10;
			}
		} catch (IOException e) {
			logger.info("解析图片长度出错!");
		}
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
					BarcodeFormat.QR_CODE, newImageWidth, newImageWidth, hints);
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
            //System.out.println(srcImagePath + " "+width + " "+height);
            // 从图片中读取RGB
			int[] ImageArrayOne = new int[width * height];
			ImageArrayOne = ImageOne.getRGB(0, 0, width, height, ImageArrayOne,
					0, width);
			// 对第二张图片做相同的处理
			File fileTwo = new File(tdFilePath);
			BufferedImage ImageTwo = ImageIO.read(fileTwo);
            //System.out.println(tdFilePath + " "+ImageTwo.getWidth() + " "+ImageTwo.getHeight()+" "+newImageWidth);
            newImageWidth = ImageTwo.getWidth();
			int[] ImageArrayTwo = new int[newImageWidth * newImageWidth];
            //System.out.println(newImageWidth);
            ImageArrayTwo = ImageTwo.getRGB(0, 0, newImageWidth, newImageWidth, ImageArrayTwo,
					0, newImageWidth);
			// 生成新图
			BufferedImage ImageNew = new BufferedImage(width,height,
					BufferedImage.TYPE_INT_RGB);
			ImageNew.setRGB(0, 0, width, height, ImageArrayOne, 0, width);// 设置左半部分的RGB
            if(ImageTwo.getWidth() < newImageWidth){
                newImageWidth = ImageTwo.getWidth();
            }
			ImageNew.setRGB(width-newImageWidth, height-newImageWidth, newImageWidth, newImageWidth,
                    ImageArrayTwo, 0, newImageWidth);// 设置右半部分的RGB

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

			//ImageIO.write(ImageNew, imgSuffix, outFile);// 写图
            writeJPG(ImageNew, new FileOutputStream(newImagePath), 1.0f);
            //删除二维码
            fileTwo.delete();
            logger.info(srcImagePath+" 二维码生成完毕");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public static void getScaledInstance(String srcImagePath,String targetImagePath, boolean higherQuality){
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(srcImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int type =
                (img.getTransparency() == Transparency.OPAQUE)
                        ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage) img;
        int w = img.getWidth(),h = img.getHeight();
        if(img.getWidth() > img.getHeight() && img.getWidth() > 1200){
            w = 1200;
            h = img.getHeight() * 1200/img.getWidth();
        }else if(img.getWidth() < img.getHeight() && img.getHeight() > 1200){
            w = img.getWidth() * 1200/img.getHeight();
            h = 1200;
        }
        int targetWidth = w;
        int targetHeight = h;
        do{
            if (higherQuality && w > targetWidth){
                w /= 2;
                if (w < targetWidth){
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight){
                h /= 2;
                if (h < targetHeight){
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);
        try {
            writeJPG(ret, new FileOutputStream(targetImagePath), 0.9f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void writeJPG(
            BufferedImage bufferedImage,
            OutputStream outputStream,
            float quality){
        Iterator<ImageWriter> iterator =
                ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter imageWriter = iterator.next();
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(quality);
        ImageOutputStream imageOutputStream =
                new MemoryCacheImageOutputStream(outputStream);
        imageWriter.setOutput(imageOutputStream);
        IIOImage iioimage = new IIOImage(bufferedImage, null, null);
        try {
            imageWriter.write(null, iioimage, imageWriteParam);
            imageOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                imageOutputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    public static void main(String[] args) {

        ImageCompose.getScaledInstance("C:\\uploadImagePath\\d.jpg","C:\\uploadImagePath\\d1.jpg",true);

    }
}
