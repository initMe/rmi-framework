package test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.exception.Error;
import com.utils.JsonUtil;

public class EnumTest {
	public EnumTest() throws Exception {
		EnumBean eb = new EnumBean();
		eb.setError(Error.cache_lock);
		eb.setCode(Error.cache_lock.getCode());
		System.out.println(testStream(eb).getError());
		System.out.println(testJson(eb).getError());
	}
	
	public EnumBean testStream(EnumBean eb) throws Exception {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bao);
		oos.writeObject(eb);
		byte[] bts = bao.toByteArray();
		
		
		ByteArrayInputStream bai = new ByteArrayInputStream(bts);
		ObjectInputStream ois = new ObjectInputStream(bai);
		return (EnumBean) ois.readObject();
	}
	
	public EnumBean testJson(EnumBean eb) throws Exception {
		String str = JsonUtil.objToJson(eb);
		return JsonUtil.jsonToBean(str, EnumBean.class);
	}
	
	public static void main(String[] args) {
		try {
			new EnumTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
