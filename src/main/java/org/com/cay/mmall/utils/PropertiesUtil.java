package org.com.cay.mmall.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by Caychen on 2018/7/5.
 */
public class PropertiesUtil {

	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

	private static Properties props;

	static{
		String propertiesName = "mmall.properties";
		props = new Properties();
		try {
			props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(propertiesName), "UTF-8"));
		} catch (IOException e) {
			logger.error("配置文件读取异常: ", e);
			e.printStackTrace();
		}
	}

	public static String getProperty(String key){
		String value = props.getProperty(key.trim());

		return StringUtils.isBlank(value) ? null : value.trim();
	}

	public static String getProperty(String key, String defaultValue){
		String value = props.getProperty(key.trim());

		return StringUtils.isBlank(value) ? defaultValue.trim() : value.trim();
	}
}
