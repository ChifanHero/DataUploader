package app.tasks.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
	
	public final static String BETWEEN_QUOTES = "(?:^|\\s)\\\"([^']*?)\\\"(?:$|\\s)";
	
	public static List<String> getSubstringBetweenQuotes(String original) {
		List<String> results = new ArrayList<String>();
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(original);
		while (m.find()) {
			results.add(m.group(1));
		}
    	return results;
	}


}
