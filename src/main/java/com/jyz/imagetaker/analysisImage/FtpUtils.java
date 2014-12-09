package com.jyz.imagetaker.analysisImage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

public class FtpUtils {
	protected Logger logger = Logger.getLogger(FtpUtils.class);

	private FTPClient ftpClient;
	private String fileName, strencoding;
	private String ip = "66.220.9.50"; // 服务器IP地址
	private String userName = "viiker"; // 用户
	private String userPwd = "nji98uhb"; // 密码
	private int port = 21; // 端口
	private String path = "/workshop/"; // 读取文件的存放目
	public static Map<String, Object> confMap;

	private static FtpUtils instance;

	public static FtpUtils getInstance() {
		if (instance == null) {
			instance = new FtpUtils();
		}
		return instance;
	}

	public FtpUtils() {
		this.reSet();
	}

	public void reSet() {
		// 以当前系统时间拼接文件名
		fileName = "config.properties";
		strencoding = "UTF-8";
		this.connectServer(ip, port, userName, userPwd, path);
	}

	/**
	 * @param ip
	 * @param port
	 * @param userName
	 * @param userPwd
	 * @param path
	 * @throws SocketException
	 * @throws IOException
	 *             function:连接到服务器
	 */
	public void connectServer(String ip, int port, String userName,
			String userPwd, String path) {
		ftpClient = new FTPClient();
		try {
			// 连接
			ftpClient.connect(ip, port);
			// 登录
			ftpClient.login(userName, userPwd);
			if (path != null && path.length() > 0) {
				// 跳转到指定目�?
				ftpClient.changeWorkingDirectory(path);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @throws IOException
	 *             function:关闭连接
	 */
	public void closeServer() {
		if (ftpClient.isConnected()) {
			try {
				ftpClient.logout();
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param path
	 * @return function:读取指定目录下的文件
	 * @throws IOException
	 */
	public List<String> getFileList(String path) {
		List<String> fileLists = new ArrayList<String>();
		// 获得指定目录下所有文件名
		FTPFile[] ftpFiles = null;
		try {
			ftpFiles = ftpClient.listFiles(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; ftpFiles != null && i < ftpFiles.length; i++) {
			FTPFile file = ftpFiles[i];
			if (file.isFile()) {
				fileLists.add(file.getName());
			}
		}
		return fileLists;
	}

	/**
	 * @param fileName
	 * @return function:从服务器上读取指定的文件
	 * @throws ParseException
	 * @throws IOException
	 */
	public Map<String, Object> readFile(){
		InputStream ins = null;
		confMap = new HashMap<String, Object>();
		try {
			// 从服务器上读取指定的文件
			ins = ftpClient.retrieveFileStream(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					ins, strencoding));
			String line;
			Map<String, String> userMap = new HashMap<String, String>();
			String userName = "";
			while ((line = reader.readLine()) != null) {
				if (StringUtils.isNotEmpty(line)) {
					String[] datas = line.split("=");
					if (datas.length > 1) {
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
								userName = trim;

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
			}
			if (StringUtils.isNotEmpty(userName)) {
				confMap.put(userName, userMap);
			}
			reader.close();
			if (ins != null) {
				ins.close();
			}
			ftpClient.getReply();
		} catch (IOException e) {
            logger.error("解析配置文件出错");
		}
		return confMap;
	}

	/**
	 * @param fileName
	 *            function:删除文件
	 */
	public void deleteFile(String fileName) {
		try {
			ftpClient.deleteFile(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
