package test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MapTest {
	public MapTest() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("1", "23");
		map.put("2", "333");
		map.put("4", "asd");
		
		Iterator<Entry<String, Object>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String,Object> entry = it.next();
			if (entry.getValue().equals("333")) {
				String key = entry.getKey();
				map.remove(key);
				System.out.println("ok");
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		new MapTest();
	}
}
