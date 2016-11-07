package com.context;

public interface Context {
	
	/** 私有key */
	public final String privateKey ="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAN4fAdLUeULQlTGA"+
									"T9CqOPu/zGQpMT1nmzV1K0kf1JOLBlLVZc2brfRh7ad6HKgsBMD/5zFNGTvAHrse"+
									"jaX/w0FGEV5NAfOB7kjTbwzTHfLL381RxBSAlPguGClAVKWW7OsJjNFEGN7vY2jJ"+
									"zwzLSPMHYxFSrU4qq2k1lcL5TiC7AgMBAAECgYBKFQlkqdzvveWkpuj37W4n+OZQ"+
									"mbSEiGOpxrRzJRyfQQUQ53+Wvc+c7HR5IS8hn9zOCguv08kQtpkJS71umgkiwwjC"+
									"HybJAovy45gHxrxBgTCkO0glzeZnYSYPpxuBLmMU3iZi2KpD+vsGfrq27bZtBLs7"+
									"6wDZsQu3Qi8M8OAFkQJBAPH5SeaDl/Z7TtgX7N8H3medxgWcekilw1juWWEMMZx2"+
									"NBC/xy1zjXslOdew9P8L2UBQRUuRP98vd/0j2FnhIicCQQDq/x6jPp6FgvtMhFEi"+
									"fRhhVAZchgjxWw78H7T3xViS205Gpbjvicmaq3ZMQxVVUrBhPyd+0fs0oRzKR1eR"+
									"ei1NAkAhaCFjydc5GF0SSHtTb2qtM4MbUzcuwHPv2zhLxbsztr4JcfC1vbZSwhjy"+
									"RmQMsj4UOwDWJf+DeLJAKKq9/e8RAkBwCROFRpxzEMBYeZjo9XUDKfkicXCgCQa6"+
									"4y73Faol0dKe0fG6mhl42Rg8QZtRI2wd4OsmhlvaeplsQtkX7YTdAkA00eoSxnSW"+
									"qnJA/3t//rsNRWjnam82/ao98NCTQ0s3Y2GChkZoIvpJPkP6dUcq40y0dtkUb3Jx"+
									"gSK2T+92lDW7";
	
	/** 共有key */
	public final String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDeHwHS1HlC0JUxgE/Qqjj7v8xk"+
								  "KTE9Z5s1dStJH9STiwZS1WXNm630Ye2nehyoLATA/+cxTRk7wB67Ho2l/8NBRhFe"+
								  "TQHzge5I028M0x3yy9/NUcQUgJT4LhgpQFSlluzrCYzRRBje72Noyc8My0jzB2MR"+
								  "Uq1OKqtpNZXC+U4guwIDAQAB";
	
	
	/** 远程服务的心跳维持间隔 */
	public final Long remoteService_heartbeat = 1000*20L;
	/** 远程服务的心跳检查超时 */
	public final Long remoteService_heartbeat_timeout = 1000*60L;
	
	/** 用户心跳检查超时 */
	public final Long user_heartbeat_timeout = 1000*30L;
	
	/** 对象池中对象的超时时间(60s) */
	public final Long pool_object_timeout = 1000*60*3L;
	/** 对象池的超时检查间隔(10s) */
	public final Long pool_object_checkTime = 1000*30L;
	
	/** 经纬度每1度的距离 (1度 = 110公里)(每公里 换算为0.0091度) */
	public final double point_rate = 110;
	/** 每1公里差距的经纬度 */
	public final double point_rate_km = 0.0091;
	/** 地图经纬度精确到小数点后的长度 */
	public final static int point_length = 5;
}
