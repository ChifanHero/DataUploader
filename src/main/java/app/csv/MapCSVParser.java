package app.csv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.bean.BeanProperties;
import app.bean.formatter.RestaurantFormatter;
import app.tasks.util.RegexUtil;

public class MapCSVParser extends CSVParser<Map<String, Object>>{
	
	private final static String COMMA_PLACE_HOLDER = "COMMA_PLACE_HOLDER";

	public MapCSVParser(String headerLine) {
		super(headerLine);
	}

	@Override
	public Map<String, Object> parseLine(String line) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<String> subStrings = RegexUtil.getSubstringBetweenQuotes(line);
		for (String subString : subStrings) {
			String newString = subString.replaceAll(",", COMMA_PLACE_HOLDER);
			line = line.replace(subString, newString);
		}
        String str[] = line.split(",");
        for(int i=0;i<str.length;i++){
        	String propertyName = getHeaders().get(i);
        	if (propertyName == null || propertyName.isEmpty() || !BeanProperties.contains(propertyName)) {
        		continue;
        	}
        	String propertyValue = str[i].trim().replaceAll(COMMA_PLACE_HOLDER, ",");
        	
        	if (propertyValue.contains("\"")) {
        		propertyValue = propertyValue.split("\"")[1];
        	}
        	if (BeanProperties.NAME.equals(propertyName)) {
        		propertyValue = RestaurantFormatter.formatChineseName(propertyValue);
        	} else if (BeanProperties.PHONE.equals(propertyName)) {
        		propertyValue = RestaurantFormatter.formatPhone(propertyValue);
        	} 
        	
        	result.put(propertyName, propertyValue);
        }
        return result;
	}

}
