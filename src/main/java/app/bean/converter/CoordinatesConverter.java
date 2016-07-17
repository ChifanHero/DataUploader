package app.bean.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.bean.Coordinates;

public class CoordinatesConverter {
	
	public static List<Double> objectToList(Coordinates coordinates) {
		if (coordinates == null) {
			return Collections.emptyList();
		}
		List<Double> converted = new ArrayList<Double>();
		converted.add(coordinates.getLon());
		converted.add(coordinates.getLat());
		return converted;
	}
	
	public static Coordinates listToObject(List<Double> latlng) {
		if (latlng == null || latlng.size() != 2) {
			return null;
		}
		Coordinates coordinates = new Coordinates();
		coordinates.setLat(latlng.get(1));
		coordinates.setLon(latlng.get(0));
		return coordinates;
	}

}
