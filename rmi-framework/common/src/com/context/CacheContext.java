package com.context;

/** 上下文常量定义 */
public interface CacheContext {
	/** 缓存用户注册信息 */
	public final String cache_user="user_info";
	/** 缓存用户注册信息[key:mobile, value:userId] */
	public final String cache_user_mobile_id="member_mobile_id";
	/** 缓存用户注册码信息 */
	public final String cache_user_checktext="user_checktext";
	/** 缓存用户注册码信息 */
	public final String cache_user_checktext_paypass="user_checktext_paypass";
	/** 缓存客户端请求的版本号(用于客户端缓存) */
	public final String cache_call_client_version="cache_call_version";
	/** 缓存服务端请求的返回数据 */
	public final String cache_call_server = "cache_call_server";
	/** 缓存消息服务器上未转发出去的消息(客户端未连接)(离线消息) */
	public final String cache_chat_deconnectMsg = "cache_chat_deconnectMsg";
	/** 缓存正在连接的用户和服务器的对应关系(聊天) */
	public final String cache_chat_userForServer = "cache_chat_userForServer";
	/** 缓存字典信息 */
	public final String cache_dictionary="cache_dictionary";
	
	/** 缓存用户id对应订单[receiveUserId, List[order]] */
	public final String cache_receiveUser_order = "cache_receive_order";
	/** 缓存用户id对应订单[userId, List[order]] */
	public final String cache_user_order = "cache_user_order";
	/** 缓存订单id对应订单[orderId, order] */
	public final String cache_order = "cache_order";
	
	/** 缓存计数器(key=功能类别id, value=当前数值) */
	public final String cache_index = "cache_index";
	/** 缓存计数器锁(key=功能类别id, value=计数器锁) */
	public final String cache_index_lock = "cache_index_lock";
	
	/** 短信发送超时时间(允许最短的短信发送间隔) */
	public final Long sms_checktext_reget_timeout = 1000*45L;
	
	/** 短信验证码有效时间 */
	public final Long sms_checktext_timeout = 3*1000*60L;
	
	/** 存储当前启用的APP版本信息[type, AppVersion] */
	public final String cache_version_app = "cache_version_app";
}
