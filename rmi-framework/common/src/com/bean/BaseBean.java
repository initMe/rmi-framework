package com.bean;

import java.io.Serializable;
import java.util.Map;

import com.manager.WordFilterManager;
import com.utils.LoggerUtil;
import com.utils.ObjectUtil;

/** 基类 */
public class BaseBean implements Serializable, Cloneable {
	private static final long serialVersionUID = 498226538729597654L;
	/** 内容是否敏感(1:敏感) */
	private Boolean filterword;
	
	/** 该类数据是否敏感(true:敏感) */
	public boolean hasFilterWord() {
		if(filterword == null) {
			filterword = checkFillterWord();
		}
		return filterword;
	}
	
	/**
	 * 动态检查本对象数据是否包含敏感字符 
	 * @return	校验结果(true：包含敏感字符，false：不包含敏感字符)
	 * @throws Exception
	 */
	public boolean checkFillterWord() {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> obj = ObjectUtil.getNotNullFields(this);
		for(Object val : obj.values()) {
			sb.append(val.toString());
		}
		String filterStr = WordFilterManager.getInstanse().getFilterWord(sb.toString());
		return filterword=(filterStr!=null);
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	/** 克隆自己 */
	public BaseBean cloneSelf() {
		try {
			return ObjectUtil.CloneObject(this);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				return (BaseBean) clone();
			} catch (CloneNotSupportedException e1) {
				LoggerUtil.error(this.getClass(), e1);
				return null;
			}
		}
	}
	
	public static void main(String[] args) {
//		String jsonStr = "{\"content\":\"6aqo54Gw55uS\",\"sender\":{\"address\":\"\",\"tel\":\"\",\"create_time_view\":\"\",\"description\":\"\",\"email\":\"\",\"filterWord\":false,\"id\":835,\"imei\":\"\",\"machinecode\":\"\",\"mobile\":\"13367253613\",\"nick\":\"沐沐鱼\",\"no\":\"\",\"password\":\"\",\"pic\":\"ftp://devRead:devRead123@122.225.222.181:21/woyao/resources/2015-1-15_14h/01a119bf-4578-4045-b0e5-1a3cd157f8eb.jpg\",\"qrcodestring\":\"\",\"sex\":\"\",\"shop_id\":0,\"update_time\":1,\"create_time\":0,\"status\":0},\"otherId\":696,\"time\":1421314038381,\"id\":0,\"otherType\":0,\"senderUserId\":835,\"functionNo\":101,\"type\":1}";
//		System.out.println(JsonUtil.jsonToBean(jsonStr, MsgBean.class).getSender().getFilterword());
	}
}
