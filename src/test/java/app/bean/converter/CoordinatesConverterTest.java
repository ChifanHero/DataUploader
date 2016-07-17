package app.bean.converter;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import app.bean.Coordinates;

public class CoordinatesConverterTest {

	@Test
	public void test1() {
		List<Double> latlng = Arrays.asList(new Double[]{-121.3345, 33.6789});
		Coordinates coordinates = CoordinatesConverter.listToObject(latlng);
		assertTrue(-121.3345 == coordinates.getLon());
		assertTrue(33.6789 == coordinates.getLat());
	}
	
	@Test
	public void test2() {
		Coordinates coordinates = new Coordinates();
		coordinates.setLat(33.6789);
		coordinates.setLon(-121.3345);
		List<Double> latlng = CoordinatesConverter.objectToList(coordinates);
		assertTrue(latlng.get(0) == -121.3345);
		assertTrue(latlng.get(1) == 33.6789);
	}

}
