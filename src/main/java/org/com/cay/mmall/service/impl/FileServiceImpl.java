package org.com.cay.mmall.service.impl;

import com.google.common.collect.Lists;
import org.com.cay.mmall.service.IFileService;
import org.com.cay.mmall.utils.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Caychen on 2018/7/5.
 */
@Service
public class FileServiceImpl implements IFileService {

	private final Logger logger = LoggerFactory.getLogger(IFileService.class);

	@Override
	public String upload(MultipartFile file, String path) {
		//文件名
		String fileName = file.getOriginalFilename();

		//扩展名
		String extName = fileName.substring(fileName.lastIndexOf(".") + 1);

		//上传文件的名字
		String uploadFileName = UUID.randomUUID().toString() + "." + extName;
		logger.info("开始上传文件，上传文件名:{}, 上传路径: {}, 实际文件名: {}", fileName, path, uploadFileName);

		File fileDir = new File(path);
		if (!fileDir.exists()) {
			fileDir.setWritable(true);
			fileDir.mkdirs();
		}

		File targetFile = new File(path, uploadFileName);

		try {
			file.transferTo(targetFile);

			//将targetFile上传到ftp服务器上
			FTPUtil.uploadFile(Lists.newArrayList(targetFile));

			//已经上传到ftp服务器上，删除upload文件夹下的图片
			targetFile.delete();

		} catch (IOException e) {
			logger.error("上传文件异常: ", e);
			e.printStackTrace();
			return null;
		}
		return targetFile.getName();
	}
}
