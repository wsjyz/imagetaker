package com.jyz.imagetaker.analysisImage;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class InitQiNiuUrl {
    protected static Logger logger = Logger.getLogger(InitQiNiuUrl.class);
	public static Map<String, Object> confMap;
	private static InitQiNiuUrl instance;
	private static String urlPath="http://7sbohb.com1.z0.glb.clouddn.com/Config.txt";
	private static String splitKey="#!";
	public static InitQiNiuUrl getInstance() {
		if (instance == null) {
			instance = new InitQiNiuUrl();
		}
		return instance;
	}

	
	public  Map<String, Object> readUrl() {
		confMap = new HashMap<String, Object>();
		try {
			URL url = new URL(urlPath);
			String urlsource = getURLSource(url);
			if (StringUtils.isNotEmpty(urlsource)) {
				String userName = "";
				Map<String, String> userMap = new HashMap<String, String>();
				String[] urlsources = urlsource.split(splitKey);
				for (int i = 1; i < urlsources.length; i++) {
					String urlSourceTemp=urlsources[i];
					if (StringUtils.isNotEmpty(urlSourceTemp)) {
						String[] datas = urlSourceTemp.split("=");
						String trim = datas[0].trim();
						String trim2 = datas[1].trim();
						if (trim.equals("ACCESS_KEY")
								|| trim.equals("SECRET_KEY")
								|| trim.equals("userCounts")) {
							confMap.put(trim, trim2);
						} else {
							if (trim.contains("userName")) {
								if (StringUtils.isNotEmpty(userName)) {
									confMap.put(userName, userMap);
								}
								userMap = new HashMap<String, String>();
								userMap.put("userName", trim2);
								userName = trim2;
							} else {

								if (trim.contains("domain")) {
									trim = "domain";
								}
								if (trim.contains("bucketName")) {
									trim = "bucketName";
								}
								userMap.put(trim, trim2);
							}
						}
					}
				}
				if (StringUtils.isNotEmpty(userName)) {
					confMap.put(userName, userMap);
				}
			}
		} catch (Exception e) {
			logger.info("解析配置文件出错！");
		}
		return confMap;

	}

	/**
	 * 通过网站域名URL获取该网站的源码
	 * 
	 * @param url
	 * @return String
	 * @throws Exception
	 */
	public static String getURLSource(URL url) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		InputStream inStream = conn.getInputStream(); // 通过输入流获取html二进制数据
		byte[] data = readInputStream(inStream); // 把二进制数据转化为byte字节数据
		String htmlSource = new String(data);
		return htmlSource;
	}

	/**
	 * 把二进制流转化为byte字节数组
	 * 
	 * @param instream
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream instream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1204];
		int len = 0;
		while ((len = instream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		instream.close();
		return outStream.toByteArray();
	}
}
