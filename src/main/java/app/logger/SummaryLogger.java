package app.logger;

import java.util.HashMap;
import java.util.Map;

public class SummaryLogger implements Logger{
	
	private Map<String, String> skippedRestaurants = new HashMap<String, String>();
	private int totalRowsFromFile;
	private int totalRecordsFromDB;
	private Map<String, Integer> counts = new HashMap<String, Integer>();
	private int savedCount;
	
	public void logSkippedRestaurant(String restaurantName, String reason) {
		skippedRestaurants.put(restaurantName, reason);
	}
	
	public void logTotalRows(int total) {
		totalRowsFromFile = total;
	}
	
	public void logRowsForFile(String filename, int count) {
		counts.put(filename, count);
	}
	
	public void logTotalRecordsFromDB(int total) {
		totalRecordsFromDB = total;
	}
	
	public void logTotalSaved(int total) {
		savedCount = total;
	}

	@Override
	public void print() {
		System.out.println("Summary:");
		System.out.println("Loaded " + totalRowsFromFile + " rows from files. file details:");
		for (Map.Entry<String, Integer> file : counts.entrySet()) {
			System.out.println(file.getKey() + ": " + file.getValue());
		} 
		System.out.println("Loaded " + totalRecordsFromDB + " existing records from database.");
		System.out.println("Skipped " + skippedRestaurants.size() + " restaurants. For reasons: ");
		for (Map.Entry<String, String> skipped : skippedRestaurants.entrySet()) {
			System.out.println(skipped.getKey() + ": " + skipped.getValue());
		}
		System.out.println("Successfully saved " + savedCount + " restaurants.");
	}

}
