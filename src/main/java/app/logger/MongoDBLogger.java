package app.logger;

import java.util.ArrayList;
import java.util.List;

public class MongoDBLogger implements Logger {
	
	private boolean success = true;
	private List<String> errors = new ArrayList<String>();
	private int saveCandidatesCount;
	

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public void addError(String error) {
		errors.add(error);
	}
	
	public void logCandidatesCount(int count) {
		saveCandidatesCount = count;
	}



	@Override
	public void print() {
		System.out.println(this.saveCandidatesCount + " candidates to be saved.");
		System.out.println("MongoDB status: ");
		if (success) {
			System.out.println("Successfully saved data");
		} else {
			System.out.println("Failed to save data. Reason: ");
			for (String error : errors) {
				System.out.println(error);
			}
		}
	}

}
