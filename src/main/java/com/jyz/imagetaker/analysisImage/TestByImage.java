package com.jyz.imagetaker.analysisImage;

import java.util.Map;

import com.jyz.imagetaker.Constants;

public class TestByImage {
	public static void main(String[] args) {
		AnalysisEvernote demo;
		try {
			demo = new AnalysisEvernote(Constants.AUTH_TOKEN);
			Map<String, String> map = demo.listNotes();
			for (String string : map.keySet()) {
				System.out.println(string);
				System.out.println(map.get(string));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ImageCompose imageCompose=new ImageCompose();
		String filePath="www.baidu.com";
		String fileVersion="E:/yz/image";
		String imageName="2erma";
		imageCompose.getImageGenerate(filePath, fileVersion, imageName);
		String fileVersion1="E:/yz/image/1.jpg";
		String fileVersion2="E:/yz/image/2erma.jpg";
		String fileVersion3="E:/yz/image/last.jpg";
		imageCompose.getImageCompose(fileVersion1, fileVersion2, fileVersion3);
		
	}
}
