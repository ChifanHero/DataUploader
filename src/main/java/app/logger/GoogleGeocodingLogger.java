package app.logger;

import java.util.HashMap;
import java.util.Map;

public class GoogleGeocodingLogger implements Logger { 
	
	private Map<String, String> failReason = new HashMap<String, String>();

	public void logFailReason(String address, String reason) {
		failReason.put(address, reason);
	}
	
	@Override
	public void print() {
		System.out.println("Google Geocoding status:");
		System.out.println("Failed to get info for " + failReason.size() + " addresses.");
		for (Map.Entry<String, String> reason : failReason.entrySet()) {
			System.out.println(reason.getKey() + ": " + reason.getValue());
		}
		
	}
	

}
