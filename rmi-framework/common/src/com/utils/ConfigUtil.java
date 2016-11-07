package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.exception.Error;
import com.exception.SystemException;

/** 配置文件资源读取 */
public class ConfigUtil {
	private String initConfigFileName = "init.properties";

	/** 配置文件的相对位置 */
	private String filePath = File.separator + "conf" + File.separator;

	private static ConfigUtil instanse;

	private Map<String, String> properties = new HashMap<String, String>();

	public synchronized static ConfigUtil getInstance() {
		if (instanse == null) {
			instanse = new ConfigUtil();
		}
		return instanse;
	}

	private ConfigUtil() {
		init();
	}

	/** 获取当前程序的根目录 */
	public String getRootUrl() {
		URL url = ConfigUtil.class.getResource("/");
		if (url == null) {
			return System.getProperty("user.dir") + File.separator;
		}
		String path = url.getFile();
		String osName = System.getProperty("os.name");
		// windows路径特殊处理掉开头的/
		if (osName.startsWith("win") || osName.startsWith("Win")) {
			if (path.charAt(0) == '/') {
				path = path.substring(1);
			}
		}
		return path;
	}

	/** 获取web项目的根目录 */
	public String getWebRootUrl() {
		String url = getRootUrl().replace("classes", "").replace("WEB-INF", "");
		url = url.substring(0, url.length() - 2);
		return url;
	}

	public static void main(String[] args) {
		System.out.println(ConfigUtil.getInstance().getRootUrl());
	}

	/** 读取文件 */
	public File readFile(String configFileName) throws Exception {
		// String fileName = ConfigUtil.class.getResource(filePath).getFile();
		URL url = ConfigUtil.class.getResource("/" + configFileName);
		String fileUrl = null;
		if (url == null || url.toURI().getPath() == null) {
			url = ConfigUtil.class.getResource(filePath + configFileName);
			if (url == null || url.toURI().getPath() == null) {
				fileUrl = System.getProperty("user.dir") + filePath
						+ configFileName;
			}
			else {
				fileUrl = url.toURI().getPath();
			}
		}
		else {
			fileUrl = url.toURI().getPath();
		}
		File file = new File(fileUrl);
		FileInputStream input = new FileInputStream(file);
		Properties p = new Properties();
		p.load(input);
		Set<String> pNames = p.stringPropertyNames();
		if (pNames == null || pNames.size() == 0) {
			return file;
		}
		for (String pname : pNames) {
			properties.put(pname, p.getProperty(pname));
		}
		return file;
	}

	/**
	 * 初始化
	 * 
	 * @param configFileName
	 *            要加载的配置文件名称
	 * @param isLoadOther
	 *            是否加载过主配置文件(主配置(init)文件)
	 */
	public void init() {
		try {
			File file = readFile(initConfigFileName);
			// 加载其他的配置文件
			File[] fileArray = file.getParentFile().listFiles();
			for (File f : fileArray) {
				if (f.isFile() && (!f.isDirectory())) {
					String fileName = f.getName();
					if (fileName.indexOf("init.") > -1) {
						readFile(fileName);
						// 加载init中对应的配置文件
						String initStr = properties.get("system.init.config");
						if (initStr != null) {
							String[] inits = initStr.split(",");
							for (String i : inits) {
								readFile(i);
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			LoggerUtil.error(this.getClass(), "配置文件读取异常", e);
			throw new SystemException(Error.system_config_read_error, e);
		}
	}

	/** 获取参数值 */
	public String getStringValue(String key, String defaultValue) {
		String value = properties.get(key);
		if (null == value || value == null || value.trim().equals("")) {
			return defaultValue;
		}
		return value.trim();
	}

	/** 获取参数值 */
	public String getStringValue(String key) {
		return getStringValue(key, "");
	}

	/** 获取参数值 */
	public Integer getIntegerValue(String key, Integer defaultValue) {
		try {
			return Integer.parseInt(getStringValue(key, String
					.valueOf(defaultValue)));
		}
		catch (Exception e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

	/** 获取参数值 */
	public Integer getIntegerValue(String key) {
		return getIntegerValue(key, 0);
	}

	/** 获取缓存的IP */
	public String getCacheIp() {
		return getStringValue("cache.jedis.address", "202.91.241.85");
	}

	/** 获取缓存的IP */
	public int getCacheProt() {
		String value = getStringValue("cache.jedis.port", "6321");
		try {
			return Integer.parseInt(value);
		}
		catch (Exception e) {
			LoggerUtil.error(this.getClass(), "value=" + value, e);
			return 6321;
		}
	}

	/** 获取缓存最大的活动链接 */
	public int getCacheMaxActive() {
		String value = getStringValue("cache.jedis.maxActive", "2000");
		try {
			return Integer.parseInt(value);
		}
		catch (Exception e) {
			return 200;
		}
	}

	/** 获取缓存初始链接数 */
	public int getCacheMaxIdle() {
		String value = getStringValue("cache.jedis.maxIdle", "5");
		try {
			return Integer.parseInt(value);
		}
		catch (Exception e) {
			return 5;
		}
	}

	/** 获取缓存最大的等待时长 */
	public int getCacheMaxWait() {
		String value = getStringValue("cache.jedis.maxWait", "1000");
		try {
			return Integer.parseInt(value);
		}
		catch (Exception e) {
			return 5;
		}
	}

	/** 获取缓存的前缀标记(即所有缓存的key都会补上此前缀) */
	public String getCacheFlag() {
		return getStringValue("cache.jedis.flag", "");
	}

	/** 获取缓存服务接口的端口号 */
	public int getCacheServicePort() {
		String value = getStringValue("cache.service.rmi.port", "37010");
		try {
			return Integer.parseInt(value);
		}
		catch (Exception e) {
			return 7073;
		}
	}

	/** 获取服务rmi的socket端口号(穿外网) */
	public int getCacheServiceSocketPort() {
		String port = getStringValue("cache.service.rmi.socket.port", "37011");
		try {
			return Integer.parseInt(port);
		}
		catch (Exception e) {
			return 7073;
		}
	}

	/** 获取缓存服务接口名 */
	public String getCacheServiceName() {
		return getStringValue("cache.service.rmi.serviceName", "CacheService");
	}

	/** 获取缓存服务接口IP */
	public String getCacheServiceIp() {
		return getStringValue("cache.service.rmi.ip", "127.0.0.1");
	}

	/** 获取缓存服务接口类型 */
	public String getCacheServiceType() {
		return getStringValue("cache.service.rmi.type", "1");
	}

	/** 获取聊天服务接口的端口号 */
	public int getChatServicePort() {
		String value = getStringValue("chat.service.rmi.port", "37030");
		try {
			return Integer.parseInt(value);
		}
		catch (Exception e) {
			return 7073;
		}
	}

	/** 获取服务rmi的socket端口号(穿外网) */
	public int getChatServiceSocketPort() {
		String port = getStringValue("chat.service.rmi.socket.port", "37032");
		try {
			return Integer.parseInt(port);
		}
		catch (Exception e) {
			return 7073;
		}
	}

	/** 获取聊天服务接口的端口号 */
	public int getChatPort() {
		String value = getStringValue("chat.service.port", "37031");
		try {
			return Integer.parseInt(value);
		}
		catch (Exception e) {
			return 7073;
		}
	}

	/** 获取聊天服务接口名 */
	public String getChatServiceName() {
		return getStringValue("chat.service.rmi.serviceName", "ChatService");
	}

	/** 获取聊天服务接口IP */
	public String getChatServiceIp() {
		return getStringValue("chat.service.rmi.ip", "127.0.0.1");
	}

	/** 获取聊天服务接口类型 */
	public String getChatServiceType() {
		return getStringValue("chat.service.rmi.type", "1");
	}

	/** 获取搜索器接口的端口号 */
	public int getSearchServicePort() {
		String value = getStringValue("search.service.rmi.port", "37020");
		try {
			return Integer.parseInt(value);
		}
		catch (Exception e) {
			return 7073;
		}
	}

	/** 获取服务rmi的socket端口号(穿外网) */
	public int getSearchServiceSocketPort() {
		String port = getStringValue("search.service.rmi.socket.port", "37021");
		try {
			return Integer.parseInt(port);
		}
		catch (Exception e) {
			return 7073;
		}
	}

	/** 获取搜索器接口名 */
	public String getSearchServiceName() {
		return getStringValue("search.service.rmi.serviceName", "SearchService");
	}

	/** 获取搜索器接口IP */
	public String getSearchServiceIp() {
		return getStringValue("search.service.rmi.ip", "127.0.0.1");
	}

	/** 获取搜索器接口类型 */
	public String getSearchServiceType() {
		return getStringValue("search.service.rmi.type", "1");
	}

	/** 搜索器的我要的db文件存储位置 */
	public String getSearchUserDb() {
		return getStringValue("search.service.db.user",
				"c:/lucentindex/user_data/");
	}

	/** 获取短信服务的IP */
	public String getSMSServiceIp() {
		return getStringValue("notify.sms.service.rmi.ip", "127.0.0.1");
	}

	/** 获取短信服务的端口号 */
	public int getSMSServicePort() {
		String port = getStringValue("notify.sms.service.rmi.port", "37040");
		try {
			return Integer.parseInt(port);
		}
		catch (Exception e) {
			return 7073;
		}
	}

	/** 获取服务rmi的socket端口号(穿外网) */
	public int getSMSServiceSocketPort() {
		String port = getStringValue("notify.sms.service.rmi.socket.port",
				"37041");
		try {
			return Integer.parseInt(port);
		}
		catch (Exception e) {
			return 7073;
		}
	}

	/** 获取控制器服务的服务名 */
	public String getControlServiceName() {
		return getStringValue("control.service.rmi.name", "ControlService");
	}

	/** 获取控制器服务的IP */
	public String getControlServiceIp() {
		return getStringValue("control.service.rmi.ip", "127.0.0.1");
	}

	/** 获取控制器服务的端口号 */
	public int getControlServicePort() {
		String port = getStringValue("control.service.rmi.port", "37000");
		try {
			return Integer.parseInt(port);
		}
		catch (Exception e) {
			return 7073;
		}
	}

	/** 获取控制器服务rmi的socket端口号(穿外网) */
	public int getControlServiceSocketPort() {
		String port = getStringValue("control.service.rmi.socket.port", "37001");
		try {
			return Integer.parseInt(port);
		}
		catch (Exception e) {
			return 7073;
		}
	}

	/** 获取短信服务的服务名 */
	public String getSMSServiceName() {
		return getStringValue("notify.sms.service.rmi.name", "SMSService");
	}

	/** 获取短信服务的类型 */
	public String getSMSServiceType() {
		return getStringValue("notify.sms.service.rmi.type", "1");
	}

	/** 获取敏感级别 */
	public int getFilterwordLevel() {
		String port = getStringValue("filterword.level", "3");
		try {
			return Integer.parseInt(port);
		}
		catch (Exception e) {
			return 3;
		}
	}

	/** 获取mongo库的IP地址 */
	public String getMongoDBHost() {
		return getStringValue("mongo.db.host", "202.91.241.87");
	}

	/** 获取mongo库端口 */
	public int getMongoDBPort() {
		return getIntegerValue("mongo.db.port", 27017);
	}

	public String getMongoDBName() {
		return getStringValue("mongo.db.name", "woyao");
	}

	/** 获取主机连接数 */
	public int getMongoConnecPerHost() {
		return getIntegerValue("mongo.options.connectionsPerHost", 10);
	}

	/** 每个主机的最大线程阻塞数 */
	public int getMongoConnectionMultiplier() {
		return getIntegerValue(
				"mongo.options.threadsAllowedToBlockForConnectionMultiplier",
				10);
	}

	/** 获取线程阻塞时的最大等待时间 */
	public int getMongoConnectTimeOut() {
		return getIntegerValue("mongo.options.connectTimeout", 0);
	}

	/** Socket超时时间 */
	public int getMongoSocketTimeOut() {
		return getIntegerValue("mongo.options.connectTimeout", 0);
	}

	/** 获取搜索器接口的端口号 */
	public int getLogServicePort() {
		String value = getStringValue("log.service.rmi.port", "37060");
		try {
			return Integer.parseInt(value);
		}
		catch (Exception e) {
			return 7073;
		}
	}

	/** 获取服务rmi的socket端口号(穿外网) */
	public int getLogServiceSocketPort() {
		String port = getStringValue("log.service.rmi.socket.port", "37061");
		try {
			return Integer.parseInt(port);
		}
		catch (Exception e) {
			return 7073;
		}
	}

	/** 获取搜索器接口名 */
	public String getLogServiceName() {
		return getStringValue("log.service.rmi.serviceName", "LogService");
	}

	/** 获取搜索器接口IP */
	public String getLogServiceIp() {
		return getStringValue("log.service.rmi.ip", "127.0.0.1");
	}

	/** 获取搜索器接口类型 */
	public String getLogServiceType() {
		return getStringValue("log.service.rmi.type", "1");
	}

	public String getInvoicePicPath() {
		return getStringValue("download.root.url");
	}

}
