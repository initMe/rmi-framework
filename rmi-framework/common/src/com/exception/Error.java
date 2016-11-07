package com.exception;

/** 异常编码值 */
public enum Error {
	notKnow_error(-1, "未知异常", "网络异常，请与管理员联系"),

	system_init_error(9999, "系统初始化异常", "网络异常，请与管理员联系"),
	
	system_utils_error(10001, "工具类使用异常", "网络异常，请与管理员联系"),
	system_utils_tasker_error(10002, "处理器处理出现异常", "网络异常，请与管理员联系"),
	system_config_read_error(10101, "配置文件读取异常", "网络异常，请与管理员联系"),
	
	system_error(20000, "系统级异常", "网络异常，请与管理员联系"),
	system_remoteService_register_info_error(20001, "远程服务注册时，上传数据错误", "网络异常，请与管理员联系"),
	system_remoteService_notfind_error(20002, "注册的远程服务未定义", "网络异常，请与管理员联系"),
	system_remoteService_notopen_error(20003, "远程服务未打开", "网络异常，请与管理员联系"),
	system_remoteService_connect_error(20004, "远程服务连接失败", "网络异常，请与管理员联系"),
	system_remote_control_error(20005, "控制中心连接失败", "网络异常，请与管理员联系"),
	system_remote_exec_error(20006, "远程服务器执行失败", "网络异常，请与管理员联系"),

	system_request_error(20020, "非法请求", "网络异常，请与管理员联系"),
	system_norequest_error(20021, "未发现请求参数", "网络异常，请与管理员联系"),
	system_request_json_notjson_error(20022, "请求的json串非法，可能是客户端未做密文处理", "网络异常，请与管理员联系"),
	system_request_notcall_error(20023, "未匹配到请求的call值", "网络异常，请与管理员联系"),
	

	system_file_null_error(20100, "未找到上传的文件，客户端未选择文件", "网络异常，请与管理员联系"),
	system_file_save_error(20101, "上传文件存储异常", "网络异常，请与管理员联系"),
	system_file_close_error(20102, "上传文件写入后，关闭文件失败", "网络异常，请与管理员联系"),

	system_file_upload_error(20103, "资源上传失败", "网络异常，请与管理员联系"),
	system_file_download_error(20104, "资源下载失败", "网络异常，请与管理员联系"),
	system_file_notfind_error(20105, "资源下载失败,资源未找到", "网络异常，请与管理员联系"),
	
	
	database_error(20200, "数据库执行异常", "网络异常，请与管理员联系"),
	database_page_null(20301, "数据库分页对象为null", "网络异常，请与管理员联系"),
	database_page_min(20302, "设置数据库分页，每页显示条数太小", "网络异常，请与管理员联系"),
	
	cache_lock(20501, "缓存锁被占用!", "网络异常，请与管理员联系"),
	
	woyao_input_filterWord(50001, "发现输入信息包含敏感字符", "信息中包含非法字符，请检查后重新提交"),	
	
	db_jdbc_table_notfind(70001, "JDBC操作数据库未找到对应的映射表", "系统错误");
	/** 编码值 */
	private int code;
	/** 备注，提示 */
	private String remark; 
	/** 异常显示问题 */
	private String viewMsg;
	/**
	 * 异常枚举类
	 * @param code	编码值
	 * @param remark	备注，提示
	 */
	private Error(int code, String remark, String viewMsg) {
		this.code = code;
		this.remark = remark;
		this.viewMsg = viewMsg;
	}
	
	/** 依据异常编码值获取异常 */
	public static Error getErrorCode(int code) {
		for(Error ec : Error.values()) {
			if(ec.getCode() == code) {
				return ec;
			}
		}
		return notKnow_error;
	}
	
	public int getCode() {
		return code;
	}
	public String getRemark() {
		return remark;
	}
	public String getViewMsg() {
		return viewMsg;
	}
}
