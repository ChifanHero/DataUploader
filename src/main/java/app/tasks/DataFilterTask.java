package app.tasks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.bean.BeanProperties;
import app.bean.Coordinates;
import github.familysyan.concurrent.tasks.Task;

/**
 * @author shiyan
 * This task is mainly for filtering out existing record.
 */
public class DataFilterTask implements Task<List<Map<String, String>>>{

	private Map<String, Map<String, Object>> existing = new HashMap<String, Map<String, Object>>();
	
	public String getUniqueTaskId() {
		return this.getClass().getName();
	}

	/**
	 * Expected dependencies:</br> 1. restaurants from database;</br> 2. restaurants from file;</br> 3. address to coordinates map
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> execute(List<Object> dependencies) {
		if (dependencies == null || dependencies.size() != 3) {
			return Collections.emptyList();
		}
		List<Map<String, Object>> existingRestaurants = (List<Map<String, Object>>) dependencies.get(0);
		List<Map<String, Object>> newRestaurants = (List<Map<String, Object>>) dependencies.get(1);
		Map<String, Coordinates> coordinates = (Map<String, Coordinates>) dependencies.get(2);
		createExiting(existingRestaurants);
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> restaurant : newRestaurants) {
			String address = (String) restaurant.get(BeanProperties.ADDRESS);
			Coordinates latlon = coordinates.get(address);
			if (latlon == null) {
				continue;
			}
			String geoId = getGeoId(latlon.getLat(), latlon.getLon());
			if (existing.get(geoId) != null) {
				Map<String, Object> merged = merge(restaurant, existing.get(geoId));
				existing.put(geoId, merged);
				results.add(merged);
			} else {
				existing.put(geoId, restaurant);
				results.add(restaurant);
			}
		}
		return null;
	}

	private Map<String, Object> merge(Map<String, Object> patchElement, Map<String, Object> mainElement) {
		Map<String, Object> merged = new HashMap<String, Object>();
		if (mainElement != null) {
			for (Map.Entry<String, Object> entry : mainElement.entrySet()) {
				merged.put(entry.getKey(), entry.getValue());
			}
		}
		if (patchElement != null) {
			for (Map.Entry<String, Object> entry : patchElement.entrySet()) {
				if (merged.get(entry.getKey()) == null) {
					merged.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return merged;
	}

	@SuppressWarnings("unchecked")
	private void createExiting(List<Map<String, Object>> existingRestaurants) {
		if (existingRestaurants == null || existingRestaurants.isEmpty()) {
			return;
		}
		for (Map<String, Object> restaurant : existingRestaurants) {
			List<Double> latlon = (List<Double>) restaurant.get("coordinates");
			if (latlon == null) {
				continue;
			}
			String geoId = getGeoId(latlon.get(0), latlon.get(1));
			existing.put(geoId, restaurant);
		}
	}

	public void failedToComplete() {

	}

	public long getTimeout() {
		return 0;
	}
	
	
	private String getGeoId(double lattitude, double longitude) {
		Double lat = Double.valueOf(lattitude);
		Double newLat =new BigDecimal(lat).setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
		Double lon = Double.valueOf(longitude);
		Double newLon =new BigDecimal(lon).setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
		StringBuilder sb = new StringBuilder();
		sb.append(newLat);
		sb.append("|");
		sb.append(newLon);
		return sb.toString();
	}

}
