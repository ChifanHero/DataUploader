package app.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.bean.Coordinates;
import app.bean.converter.CoordinatesConverter;
import app.logger.StatusLogger;
import app.tasks.util.GeoId;
import github.familysyan.concurrent.tasks.Task;

/**
 * @author shiyan
 * This task is mainly for filtering out existing record.
 */
public class DataFilterTask implements Task<List<Map<String, Object>>>{

	private Map<String, Map<String, Object>> existing = new HashMap<String, Map<String, Object>>();
	private Map<String, Map<String, Object>> existingAddress = new HashMap<String, Map<String, Object>>();
	private StatusLogger statusLogger = StatusLogger.getInstance();
	
	
	public String getUniqueTaskId() {
		return this.getClass().getName();
	}

	/**
	 * Expected dependencies:</br> 1. restaurants from database;</br> 2. restaurants from file;
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> execute(List<Object> dependencies) {
		System.out.println("Starting DataFilterTask");
		if (dependencies == null || dependencies.size() != 2) {
			return Collections.emptyList();
		}
		List<Map<String, Object>> existingRestaurants = null;
		List<Map<String, Object>> newRestaurants = null;
		for (int i = 0; i < dependencies.size(); i++) {
			List<Map<String, Object>> restaurants = (List<Map<String, Object>>) (dependencies.get(i));
			if (restaurants != null && restaurants.size() > 0) {
				if (restaurants.get(0).get("_id") != null) {
					existingRestaurants = restaurants;
					newRestaurants = (List<Map<String, Object>>) (dependencies.get(1 - i));
				} else {
					newRestaurants = restaurants;
					existingRestaurants = (List<Map<String, Object>>) (dependencies.get(1 - i));
				}
			}
		}
		System.out.println(newRestaurants.size() + " restaurants before deduping");
		recordExiting(existingRestaurants);
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> restaurant : newRestaurants) {
			String geoId = (String) restaurant.get("geo_id");
			String address = (String) restaurant.get("address");
			if (existing.get(geoId) != null || existingAddress.get(address) != null) {
				Map<String, Object> existingRestaurant = null;
				if (existing.get(geoId) != null) {
					existingRestaurant = existing.get(geoId);
				}
				if (existingAddress.get(address) != null) {
					existingRestaurant = existingAddress.get(address);
				}
				if (needsToMerge(restaurant, existingRestaurant)) {
					Map<String, Object> merged = merge(restaurant, existing.get(geoId));
					existing.put(geoId, merged);
					existingAddress.put(address, merged);
					if (results.contains(existingRestaurant)) {
						results.remove(existingRestaurant);
					}
					results.add(merged);
				} else {
					String name = restaurant.get("name") != null? (String)restaurant.get("name") : (String) restaurant.get("english_name");
					statusLogger.summaryLogger.logSkippedRestaurant(name, "Completely same restaurant already existing");
				}
				
			} else {
				existing.put(geoId, restaurant);
				results.add(restaurant);
			}
		}
		System.out.println(results.size() + " restaurants after deduping");
		return results;
	}

	private boolean needsToMerge(Map<String, Object> restaurant, Map<String, Object> existing) {
		if (restaurant == null && existing == null) {
			return false;
		}
		if (restaurant == null && existing != null) {
			return false;
		}
		if (existing == null && restaurant != null) {
			return true;
		}
		Set<String> newKey = restaurant.keySet();
		filterEmptyKey(newKey, restaurant);
		Set<String> existingKey = existing.keySet();
		filterEmptyKey(existingKey, existing);
		for (String key : newKey) {
			if (!existingKey.contains(key)) {
				return true;
			} 
		}
		return false;
	}

	private void filterEmptyKey(Set<String> key, Map<String, Object> restaurant) {
		if (key == null || restaurant == null) {
			return;
		}
		Iterator<String> iterator = key.iterator();
		while(iterator.hasNext()) {
			String k = iterator.next();
			if (restaurant.get(k) == null) {
				iterator.remove();
			} else {
				if (restaurant.get(k) instanceof String) {
					String value = (String) restaurant.get(k);
					if (value.trim().isEmpty()) {
						iterator.remove();
					}
				}
			}
		}
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
	private void recordExiting(List<Map<String, Object>> existingRestaurants) {
		if (existingRestaurants == null || existingRestaurants.isEmpty()) {
			return;
		}
		for (Map<String, Object> restaurant : existingRestaurants) {
			String geoId = (String) restaurant.get("geo_id");
			String address = (String) restaurant.get("address");
			if (geoId == null) {
				List<Double> latlon = (List<Double>) restaurant.get("coordinates");
				if (latlon == null) {
					continue;
				}
				Coordinates coordinates = CoordinatesConverter.listToObject(latlon);
				geoId = GeoId.from(coordinates.getLat(), coordinates.getLon());
			}
			existing.put(geoId, restaurant);
			existingAddress.put(address, restaurant);
		}
	}

	public void failedToComplete() {

	}

	public long getTimeout() {
		return 0;
	}

}
