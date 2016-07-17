package app.tasks.util;

import java.math.BigDecimal;

public class GeoId {
	
	public static String from(double lattitude, double longitude) {
		Double lat = Double.valueOf(lattitude);
		Double newLat =new BigDecimal(lat).setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
		Double lon = Double.valueOf(longitude);
		Double newLon =new BigDecimal(lon).setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
		StringBuilder sb = new StringBuilder();
		sb.append(newLat);
		sb.append("|");
		sb.append(newLon);
		return sb.toString();
	}

}
