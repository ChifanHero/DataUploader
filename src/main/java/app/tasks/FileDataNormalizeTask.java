package app.tasks;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import app.bean.BeanProperties;
import app.bean.LocationInfo;
import app.bean.converter.CoordinatesConverter;
import app.logger.StatusLogger;
import app.tasks.util.GeoId;
import github.familysyan.concurrent.tasks.Task;

public class FileDataNormalizeTask implements Task<List<Map<String, Object>>>{

	private StatusLogger statusLogger = StatusLogger.getInstance();
	
	
	@Override
	public String getUniqueTaskId() {
		return this.getClass().getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> execute(List<Object> dependencies) {
		if (dependencies == null || dependencies.size() != 2) {
			return Collections.emptyList();
		}
		Map<String, LocationInfo> locationInfo = null;
		List<Map<String, Object>> newRestaurants = null;
		for (Object dependency : dependencies) {
			if (dependency instanceof Map) {
				locationInfo = (Map<String, LocationInfo>) dependency;
			} else if (dependency instanceof List) {
				newRestaurants = (List<Map<String, Object>>) dependency;
			}
		}
		if (newRestaurants != null) {
			Iterator<Map<String, Object>> iterator = newRestaurants.iterator();
			while (iterator.hasNext()) {
				Map<String, Object> newRestaurant = iterator.next();
				String address = (String) newRestaurant.get(BeanProperties.ADDRESS);
				if (locationInfo != null && locationInfo.get(address) != null) {
					LocationInfo info = locationInfo.get(address);
					newRestaurant.put(BeanProperties.ADDRESS, info.getFormattedAddress());
					List<Double> coordinates = CoordinatesConverter.objectToList(info.getCoordinates());
					newRestaurant.put("coordinates", coordinates);
					newRestaurant.put("geo_id", GeoId.from(info.getCoordinates().getLat(), info.getCoordinates().getLon()));
				} else {
					iterator.remove();
					String name = newRestaurant.get("name") != null? (String)newRestaurant.get("name") : (String) newRestaurant.get("english_name");
					statusLogger.summaryLogger.logSkippedRestaurant(name, "Not able to get geo info from google.");
				}
			}
			
		}
		return newRestaurants;
		
	}

	@Override
	public void failedToComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

}
