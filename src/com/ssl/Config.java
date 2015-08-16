package com.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {
	private static Properties properties;
	private static String path;
	private static String workPath = ".";
	public Config(){
	}
	public static String getValue(String key){
		if(properties == null){
			properties = new Properties();
			path = Config.class.getResource("/").getPath();
			try {
				properties.load(Config.class.getResourceAsStream("/config/config.properties"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return properties.getProperty(key);
	}
	public static String getValuePath(String key){
		if(properties == null){
			properties = new Properties();
			path = Config.class.getResource("/").getPath();
			try {
				properties.load(Config.class.getResourceAsStream("/config/config.properties"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return path + properties.getProperty(key);
	}
	/**
	 * 返回程序工作的根目录
	 * @return
	 */
	public static String getWorkPath(){
		return workPath;
	}
	public static void main(String args[]){
		System.out.println(Config.getValue("ClientKeyStoreFile"));
		System.out.println(Config.class.getResource("/").getPath());
	}
}
