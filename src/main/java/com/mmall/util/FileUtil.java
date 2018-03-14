package com.mmall.util;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {
	
	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	public static String upload(MultipartFile file , String path){
		String originalFilename = file.getOriginalFilename();
		//获取后缀名
		String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
		String newFileName = UUID.randomUUID().toString() + "." + extension;
		File dir = new File(path);
		if (!dir.exists()){
			dir.canWrite();
			dir.mkdirs();
		}
		File uploadFile = new File(path , newFileName);
		logger.warn("上传文件名：{} , 上传路径：{} , 新的文件名：{}" , originalFilename , path , newFileName);

		try{
			//上传至应用服务器
			file.transferTo(uploadFile);
			//上传至FTP服务器
			boolean result = FTPUtil.upload2FTP(Arrays.asList(uploadFile));
			//删除上传的临时文件
			boolean delete = uploadFile.delete();
			if (result){
				return uploadFile.getName();
			}
			logger.warn("删除本地文件{}", delete ? "Success" : "Failed");
		}catch (Exception e){
			logger.error("上传文件异常",e);
			return null;
		}
		return null;
	}
}
