package app.csv;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class CSVParser<E> {
	
	private Map<Integer, String> headers;
	
	public CSVParser(String headerLine) {
		String[] headers = headerLine.split(",");
		if (headers != null && headers.length > 0) {
			Map<Integer, String> result = new HashMap<Integer, String>();
			for (int i = 0; i < headers.length; i++) {
				result.put(i, headers[i].trim());
			}
			this.headers = result;
		} else {
			this.headers = Collections.emptyMap();
		}
	}
	
	public Map<Integer, String> getHeaders() {
		return headers;
	}
	
	public abstract E parseLine(String line);

}
