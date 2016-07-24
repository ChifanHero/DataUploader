package app.tasks;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import app.bean.Coordinates;
import app.bean.LocationInfo;
import app.google.http.GoogleGeocodingClient;
import app.google.http.response.GeocodingResponse;
import app.google.http.response.GeocodingResult;
import app.google.http.response.Geometry;
import app.google.http.response.Location;
import app.logger.StatusLogger;
import github.familysyan.concurrent.tasks.Task;
import github.familysyan.concurrent.tasks.TaskConfiguration;
import github.familysyan.concurrent.tasks.orchestrator.Orchestrator;

/**
 * @author shiyan
 * This task is for calling google geocoding api.
 */
public class GeocodingTask implements Task<Map<String, LocationInfo>>{

	private Orchestrator orchestrator;
	private String apiKey;
	
	public GeocodingTask(Orchestrator orchestrator, String apiKey) {
		this.orchestrator = orchestrator;
		this.apiKey = apiKey;
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
	public Map<String, LocationInfo> execute(List<Object> dependencies) {
		System.out.println("Starting Geocoding Task");
		if (dependencies == null || dependencies.size() != 1) {
			return Collections.emptyMap();
		}
		List<Map<String, Object>> restaurants = (List<Map<String, Object>>) dependencies.get(0);
		System.out.println(restaurants.size() + " restaurants need geo info");
		AggregateTask aggregateTask = new AggregateTask();
		TaskConfiguration tc = new TaskConfiguration(aggregateTask);
		for (int i = 1; i <= restaurants.size(); i++) {
			SubTask subTask = new SubTask(i, (String) restaurants.get(i - 1).get("address"), apiKey);
			orchestrator.acceptTask(subTask);
			tc.addDependency(subTask);
			if (i % 40 == 0) { // pause for 1.5 seconds. Because geocoding api has limit of 50 req/s.
				try {
				    Thread.sleep(2000);                 
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			}
		}
		orchestrator.acceptTask(aggregateTask, tc);
		Map<String, LocationInfo> result = null;
		try {
			result = (Map<String, LocationInfo>) orchestrator.getTaskResult(aggregateTask.getUniqueTaskId());
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			System.err.println("Error executing GeocodingTask. Abort data upload.");
			System.exit(0);
		}
		System.out.println(result.size() + " restaurants get geo info");
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
	
	private static class SubTask implements Task<Map<String, LocationInfo>> {

		private int id = 0;
		private String address;
		private String apiKey;
		
		public SubTask(int id, String address, String apiKey) {
			this.id = id;
			this.address = address;
			this.apiKey = apiKey;
		}
		
		@Override
		public String getUniqueTaskId() {
			return this.getClass().getName() + id;
		}

		@Override
		public Map<String, LocationInfo> execute(List<Object> dependencies) {
//			System.out.println("Starting geo subtask");
			Map<String, LocationInfo> locationInfo = new HashMap<String, LocationInfo>();
			try {
				GoogleGeocodingClient client = new GoogleGeocodingClient(apiKey);
				GeocodingResponse response = client.get(address);
				if (response != null) {
					if (response.getErrorMessage() != null) {
						System.err.println(address + ": " + response.getErrorMessage());
						StatusLogger.getInstance().geoCodingLogger.logFailReason(address, response.getErrorMessage());
						return Collections.emptyMap();
					}
					List<GeocodingResult> results = response.getResults();
					if (results != null && results.size() != 0) {
						if (results.size() == 1) {
							GeocodingResult result = results.get(0);
							Geometry geometry = result.getGeometry();
							String formattedAddress = result.getFormattedAddress();
							LocationInfo info = new LocationInfo();
							locationInfo.put(address, info);
							if (geometry != null) {
								Location location = geometry.getLocation();
								if (location != null) {
									double lat = location.getLat();
									double lon = location.getLng();
									Coordinates coordinates = new Coordinates();
									coordinates.setLat(lat);
									coordinates.setLon(lon);
									info.setCoordinates(coordinates);
								} else {
									StatusLogger.getInstance().geoCodingLogger.logFailReason(address, "Not able to get lat&lng for this address");
								}
							} else {
								StatusLogger.getInstance().geoCodingLogger.logFailReason(address, "Not able to get geometry for this address");
							}
							if (formattedAddress != null) {
								info.setFormattedAddress(formattedAddress);
							} else {
								StatusLogger.getInstance().geoCodingLogger.logFailReason(address, "Not able to format this address");
							}
						} else {
							StatusLogger.getInstance().geoCodingLogger.logFailReason(address, "Ambigous address. " + results.size() + " results found.");
						}
					} else {
						StatusLogger.getInstance().geoCodingLogger.logFailReason(address, "Did not get info for this address");
					}
				} else {
					StatusLogger.getInstance().geoCodingLogger.logFailReason(address, "Get null response from Google.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Geocoding api has error. Current address is " + this.address);
				System.exit(0);
			}
			
			return locationInfo;
		}

		@Override
		public void failedToComplete() {
			System.err.println("not able to call geocoding api within 10s. upload data abort");
			System.exit(0);
		}

		@Override
		public long getTimeout() {
			return 10000;
		}
		
	}
	
	private static class AggregateTask implements Task<Map<String, LocationInfo>> {

		@Override
		public String getUniqueTaskId() {
			return this.getClass().getName();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Map<String, LocationInfo> execute(List<Object> dependencies) {
			if (dependencies == null) {
				return Collections.emptyMap();
			}
			Map<String, LocationInfo> results = new HashMap<String, LocationInfo>();
			for (Object dependency : dependencies) {
				if (dependency != null && dependency instanceof Map) {
					Map<String, LocationInfo> map = (Map<String, LocationInfo>) dependency;
					results.putAll(map);
				}
				
			}
			System.out.println("Sucessfully get geo info for " + results.size() + " restaurants");
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
