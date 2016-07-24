package app.bean.formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestaurantFormatter {
	
	public static String formatChineseName(String original) {
		if (original == null) {
			return null;
		}
		if (original.isEmpty()) {
			return "";
		}
		if (!containsHanScript(original)) {
			return original;
		}
		StringBuilder sb = new StringBuilder();
		char[] chars = original.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			int codePoint = Character.codePointAt(chars, i);
			if (Character.UnicodeScript.of(codePoint) == Character.UnicodeScript.HAN) {
				sb.append(chars[i]);
			}
		}
		return sb.toString();
	}
	
	public static String formatPhone(String original) {
		if (original == null) {
			return null;
		}
		Pattern p = Pattern.compile("\\d");
		Matcher m = p.matcher(original);
		List<String> numbers = new ArrayList<String>();
		while (m.find()) {
			numbers.add(m.group());
		}
		StringBuilder sb = new StringBuilder();
		if (numbers.size() == 10) {
			sb.append("(");
			for (int i = 0; i < 3; i++) {
				sb.append(numbers.get(i));
			}
			sb.append(")");
			sb.append(" ");
			for (int i = 3; i < 6; i++) {
				sb.append(numbers.get(i));
			}
			sb.append("-");
			for (int i = 6; i < 10; i++) {
				sb.append(numbers.get(i));
			}
		} else {
			for (String number : numbers) {
				sb.append(number);
			}
		}
		return sb.toString();
	}
	

	public static boolean containsHanScript(String s) {
	    return s.codePoints().anyMatch(
	            codepoint ->
	            Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN);
	
	}

}
