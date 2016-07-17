package app.tasks;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class DataFilterTaskTest {

	@Test
	public void test() {
		Map<String, Object> restaurant1 = new HashMap<String, Object>();
		restaurant1.put("name", "韶山冲");
		restaurant1.put("address", "5152 moorpark ave, san jose");
		restaurant1.put("geo_id", "31.1234|-121.5678");
		Map<String, Object> restaurant2 = new HashMap<String, Object>();
		restaurant2.put("name", "韶山印象");
		restaurant2.put("english_name", "Hunan Impression");
		restaurant2.put("address", "2855 moorpark ave, san jose");
		restaurant2.put("geo_id", "31.2345|-121.5678");
		Map<String, Object> restaurant3 = new HashMap<String, Object>();
		restaurant3.put("name", "韶山印象2");
		restaurant3.put("address", "1234 moorpark ave, san jose");
		restaurant3.put("geo_id", "31.3456|-121.5678");
		List<Map<String, Object>> newRestaurants = new ArrayList<Map<String, Object>>();
		newRestaurants.add(restaurant1);
		newRestaurants.add(restaurant2);
		newRestaurants.add(restaurant3);
		
		Map<String, Object> existing1 = new HashMap<String, Object>();
		existing1.put("name", "韶山冲");
		existing1.put("address", "5152 moorpark ave, san jose");
		existing1.put("geo_id", "31.1234|-121.5678");
		existing1.put("_id", "2wsx3edc4r");
		Map<String, Object> existing2 = new HashMap<String, Object>();
		existing2.put("name", "韶山印象");
		existing2.put("address", "2855 moorpark ave, san jose");
		existing2.put("geo_id", "31.2345|-121.5678");
		existing2.put("_id", "1qaz2wsx3e");
		List<Map<String, Object>> existings = new ArrayList<Map<String, Object>>();
		existings.add(existing1);
		existings.add(existing2);
		
		List<Object> dependencies = new ArrayList<Object>();
		dependencies.add(newRestaurants);
		dependencies.add(existings);
		
		DataFilterTask task = new DataFilterTask();
		List<Map<String, Object>> results = task.execute(dependencies);
		assertEquals(2, results.size());
		assertEquals("韶山印象",results.get(0).get("name"));
		assertEquals("2855 moorpark ave, san jose", results.get(0).get("address"));
		assertEquals("Hunan Impression", results.get(0).get("english_name"));
		assertEquals("31.2345|-121.5678", results.get(0).get("geo_id"));
		assertEquals("1qaz2wsx3e", results.get(0).get("_id"));
		assertEquals(5, results.get(0).size());
		assertEquals("韶山印象2",results.get(1).get("name"));
		assertEquals("1234 moorpark ave, san jose", results.get(1).get("address"));
		assertEquals("31.3456|-121.5678", results.get(1).get("geo_id"));
		assertEquals(3, results.get(1).size());
	}

}
