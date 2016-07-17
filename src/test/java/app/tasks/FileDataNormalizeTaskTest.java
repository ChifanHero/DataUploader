package app.tasks;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import app.bean.Coordinates;
import app.bean.LocationInfo;

public class FileDataNormalizeTaskTest {

	@Test
	public void test() {
		Map<String, Object> restaurant1 = new HashMap<String, Object>();
		restaurant1.put("name", "韶山冲");
		restaurant1.put("address", "5152 moorpark ave, san jose");
		Map<String, Object> restaurant2 = new HashMap<String, Object>();
		restaurant2.put("name", "韶山印象");
		restaurant2.put("address", "2855 moorpark ave, san jose");
		Map<String, Object> restaurant3 = new HashMap<String, Object>();
		restaurant3.put("name", "韶山印象2");
		restaurant3.put("address", "1234 moorpark ave, san jose");
		List<Map<String, Object>> restaurants = new ArrayList<Map<String, Object>>();
		restaurants.add(restaurant1);
		restaurants.add(restaurant2);
		restaurants.add(restaurant3);
		Map<String, LocationInfo> infos = new HashMap<String, LocationInfo>();
		LocationInfo info1 = new LocationInfo();
		info1.setFormattedAddress("5152 Moorpark Ave, San Jose, CA, 96123");
		Coordinates coordinates1 = new Coordinates();
		coordinates1.setLat(37.12345);
		coordinates1.setLon(-121.45678);
		info1.setCoordinates(coordinates1);
		infos.put("5152 moorpark ave, san jose", info1);
		LocationInfo info2 = new LocationInfo();
		info2.setFormattedAddress("2855 Moorpark Ave, San Jose, CA, 96123");
		Coordinates coordinates2 = new Coordinates();
		coordinates2.setLat(37.12345);
		coordinates2.setLon(-121.45678);
		info2.setCoordinates(coordinates2);
		infos.put("2855 moorpark ave, san jose", info2);
		List<Object> dependencies = new ArrayList<Object>();
		dependencies.add(infos);
		dependencies.add(restaurants);
		FileDataNormalizeTask task = new FileDataNormalizeTask();
		List<Map<String, Object>> results = task.execute(dependencies);
		assertEquals(2, results.size());
		assertEquals("5152 Moorpark Ave, San Jose, CA, 96123", results.get(0).get("address"));
		assertEquals("37.1234|-121.4567", results.get(0).get("geo_id"));
		assertEquals("2855 Moorpark Ave, San Jose, CA, 96123", results.get(1).get("address"));
		assertEquals("37.1234|-121.4567", results.get(1).get("geo_id"));
	}

}
