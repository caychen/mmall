package org.com.cay.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Caychen on 2018/7/5.
 */
public interface IFileService {

	String upload(MultipartFile file, String path);
}
