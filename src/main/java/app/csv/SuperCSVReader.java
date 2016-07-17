package app.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

public class SuperCSVReader {

	public static List<Map<String, Object>> read(String filepath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filepath));

		String headerLine = br.readLine();
		if (headerLine == null) {
			br.close();
			return null;
		}
		MapCSVParser csvParser = new MapCSVParser(headerLine);
		Map<Integer, String> headers = csvParser.getHeaders();

		ICsvMapReader mapReader = null;
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		
		try {
			mapReader = new CsvMapReader(new FileReader(filepath), CsvPreference.STANDARD_PREFERENCE);

			mapReader.getHeader(true); 

			String[] header = new String[headers.size()];
			CellProcessor[] processors = new CellProcessor[headers.size()];
			for (int i = 0; i < header.length; i++) {
				header[i] = headers.get(i);
				processors[i] = null;
			}
			Map<String, Object> customerMap;
			while ((customerMap = mapReader.read(header, processors)) != null) {
				results.add(customerMap);
			}

		} finally {
			if (mapReader != null) {
				mapReader.close();
				br.close();
			}
		}
		return results;
	}

}
