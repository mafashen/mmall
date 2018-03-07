package com.mmall.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPUtil {

	private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

	private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
	private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
	private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");
	private static int port = 21;

	public static void upload2FTP(List<File> files) throws IOException {
		logger.warn("开始连接ftp服务器...");
		boolean ret = upload(files , "img");
		logger.warn("上传文件完成，上传结果：{}" , ret ? "Success" : "Failed");
	}

	private static boolean upload(List<File> fileList , String remotePath) throws IOException {
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		try {
			connect(remotePath, ftpClient);

			for (File file : fileList) {
				fis = new FileInputStream(file);
				ftpClient.storeFile(remotePath , fis);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			fis.close();
			ftpClient.disconnect();
		}
		return false;
	}

	private static void connect(String remotePath, FTPClient ftpClient) throws IOException {
		ftpClient.connect(ftpIp);
		ftpClient.login(ftpUser, ftpPass);

		ftpClient.setBufferSize(1024);
		ftpClient.setControlEncoding("utf-8");
		//更换工作目录
		ftpClient.changeWorkingDirectory(remotePath);
		//设置文件烈性
		ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
		ftpClient.enterLocalActiveMode();
	}
}
