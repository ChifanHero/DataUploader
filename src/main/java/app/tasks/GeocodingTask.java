package app.tasks;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import app.bean.Coordinates;
import app.config.GoogleConfig;
import github.familysyan.concurrent.tasks.Task;
import github.familysyan.concurrent.tasks.TaskConfiguration;
import github.familysyan.concurrent.tasks.orchestrator.Orchestrator;

/**
 * @author shiyan
 * This task is for calling google geocoding api.
 */
public class GeocodingTask implements Task<Map<String, Coordinates>>{

	private Orchestrator orchestrator;
	
	public GeocodingTask(Orchestrator orchestrator) {
		this.orchestrator = orchestrator;
	}
	
	@Override
	public String getUniqueTaskId() {
		return this.getClass().getName();
	}

	/**
	 * Expected dependencies:</br> 1. restaurants with address;
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Coordinates> execute(List<Object> dependencies) {
		if (dependencies == null || dependencies.size() != 1) {
			return Collections.emptyMap();
		}
		List<Map<String, Object>> restaurants = (List<Map<String, Object>>) dependencies.get(0);
		AggregateTask aggregateTask = new AggregateTask();
		TaskConfiguration tc = new TaskConfiguration(aggregateTask);
		for (int i = 1; i <= restaurants.size(); i++) {
			SubTask subTask = new SubTask(i, (String) restaurants.get(i - 1).get("address"));
			orchestrator.acceptTask(subTask);
			tc.addDependency(subTask);
			if (i % 50 == 0) { // pause for 1.1 seconds. Because geocoding api has limit of 50 req/s.
				try {
				    Thread.sleep(1100);                 
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			}
		}
		orchestrator.acceptTask(aggregateTask, tc);
		Map<String, Coordinates> result = null;
		try {
			result = (Map<String, Coordinates>) orchestrator.getTaskResult(aggregateTask.getUniqueTaskId());
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			System.err.println("Error executing GeocodingTask. Abort data upload.");
			System.exit(0);
		}
		return result;
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
	
	private static class SubTask implements Task<Map<String, Coordinates>> {

		private int id = 0;
		private String address;
		
		public SubTask(int id, String address) {
			this.id = id;
			this.address = address;
		}
		
		@Override
		public String getUniqueTaskId() {
			return this.getClass().getName() + id;
		}

		@Override
		public Map<String, Coordinates> execute(List<Object> dependencies) {
			GeoApiContext context = new GeoApiContext().setApiKey(GoogleConfig.GEOCODING_API_KEY);
			GeocodingResult[] results = null;
			try {
				results = GeocodingApi.geocode(context, this.address).await();
				if (results != null && results.length == 1) {
					GeocodingResult result = results[0];
					if (result != null && result.geometry != null && result.geometry.location != null) {
						LatLng latlng = result.geometry.location;
						Map<String, Coordinates> latlngMap = new HashMap<String, Coordinates>();
						Coordinates coordinates = new Coordinates();
						coordinates.setLat(latlng.lat);
						coordinates.setLon(latlng.lng);
						latlngMap.put(address, coordinates);
						return latlngMap;
					}
				} 
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Geocoding api has error. Current address is " + this.address);
				System.exit(0);
			}
			
			return Collections.emptyMap();
		}

		@Override
		public void failedToComplete() {
			System.err.println("not able to call geocoding api within 2s. upload data abort");
			System.exit(0);
		}

		@Override
		public long getTimeout() {
			return 2000;
		}
		
	}
	
	private static class AggregateTask implements Task<Map<String, Coordinates>> {

		@Override
		public String getUniqueTaskId() {
			return this.getClass().getName();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Map<String, Coordinates> execute(List<Object> dependencies) {
			if (dependencies == null) {
				return Collections.emptyMap();
			}
			Map<String, Coordinates> results = new HashMap<String, Coordinates>();
			for (Object dependency : dependencies) {
				if (dependency != null && dependency instanceof Map) {
					Map<String, Coordinates> map = (Map<String, Coordinates>) dependency;
					results.putAll(map);
				}
				
			}
			return results;
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

}
