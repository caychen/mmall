package org.com.cay.mmall.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Caychen on 2018/7/5.
 */
public class FTPUtil {

	private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

	private static String FTP_IP = PropertiesUtil.getProperty("ftp.server.ip");
	private static String FTP_USER = PropertiesUtil.getProperty("ftp.user");
	private static String FTP_PASS = PropertiesUtil.getProperty("ftp.pass");

	private String ip;
	private int port;
	private String user;
	private String pwd;
	private FTPClient ftpClient;

	public FTPUtil(String ip, int port, String user, String pwd) {
		this.ip = ip;
		this.port = port;
		this.user = user;
		this.pwd = pwd;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public FTPClient getFtpClient() {
		return ftpClient;
	}

	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}

	public static boolean uploadFile(List<File> fileList) throws IOException {
		FTPUtil ftpUtil = new FTPUtil(FTP_IP, 21, FTP_USER, FTP_PASS);

		logger.info("开始连接ftp服务");
		boolean result = ftpUtil.uploadFile("img", fileList);
		logger.info("开始连接ftp服务器，结束上传，结果：{}", result);

		return result;
	}

	private boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
		boolean uploaded = true;
		FileInputStream fis = null;

		//连接ftp服务器
		if (connectFtpServer(this.ip, this.user, this.pwd)) {
			try {
				//切换工作目录
				ftpClient.changeWorkingDirectory(remotePath);
				ftpClient.setBufferSize(1024);
				ftpClient.setControlEncoding("UTF-8");
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				ftpClient.enterLocalPassiveMode();

				//遍历上传文件
				for (File file : fileList) {
					fis = new FileInputStream(file);
					ftpClient.storeFile(file.getName(), fis);
				}
			} catch (IOException e) {
				logger.error("上传文件异常: ", e);
				uploaded = false;
				e.printStackTrace();
			} finally {
				if (fis != null) {
					fis.close();
				}
				ftpClient.disconnect();
			}
		}
		return uploaded;
	}

	private boolean connectFtpServer(String ip, String user, String pwd) {
		boolean isSuccess = false;
		ftpClient = new FTPClient();
		try {
			//连接ftp
			ftpClient.connect(ip);
			//登录ftp服务器
			isSuccess = ftpClient.login(user, pwd);

		} catch (IOException e) {
			logger.error("连接ftp服务器异常: ", e);
			e.printStackTrace();
		}
		return isSuccess;
	}
}
