package app.logger;

import java.util.ArrayList;
import java.util.List;

public class MongoDBLogger implements Logger {
	
	private boolean success = true;
	private List<String> errors = new ArrayList<String>();
	

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public void addError(String error) {
		errors.add(error);
	}



	@Override
	public void print() {
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
