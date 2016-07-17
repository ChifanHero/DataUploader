package app.google.http;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

import app.config.GoogleConfig;
import app.google.http.response.GeocodingResponse;
import app.google.http.response.GeocodingResult;
import app.google.http.response.Geometry;
import app.google.http.response.Location;

public class GoogleGeocodingClientTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		GoogleGeocodingClient client = new GoogleGeocodingClient(GoogleConfig.GEOCODING_API_KEY);
		String address = "5152 Moorpark Ave, San Jose, CA";
		GeocodingResponse response = client.get(address);
		assertTrue(response != null);
		assertTrue(response.getResults() != null);
		assertTrue(response.getResults().size() == 1);
		List<GeocodingResult> results = response.getResults();
		GeocodingResult result = results.get(0);
		assertTrue(result.getFormattedAddress() != null);
		assertTrue(result.getGeometry() != null);
		Geometry geometry = result.getGeometry();
		assertTrue(geometry.getLocation() != null);
		Location location = geometry.getLocation();
		assertTrue(location.getLat() != 0);
		assertTrue(location.getLng() != 0);
	}
	
	@Test
	public void test2() throws ClientProtocolException, IOException {
		GoogleGeocodingClient client = new GoogleGeocodingClient(GoogleConfig.GEOCODING_API_KEY);
		String address = "930 N 130th St, Seattle, WA 98133";
		GeocodingResponse response = client.get(address);
		assertTrue(response != null);
		assertTrue(response.getResults() != null);
		assertTrue(response.getResults().size() == 1);
		List<GeocodingResult> results = response.getResults();
		GeocodingResult result = results.get(0);
		assertTrue(result.getFormattedAddress() != null);
		assertTrue(result.getGeometry() != null);
		Geometry geometry = result.getGeometry();
		assertTrue(geometry.getLocation() != null);
		Location location = geometry.getLocation();
		assertTrue(location.getLat() != 0);
		assertTrue(location.getLng() != 0);
	}
	
	@Test
	public void test3() throws ClientProtocolException, IOException {
		GoogleGeocodingClient client = new GoogleGeocodingClient(GoogleConfig.GEOCODING_API_KEY);
		String address = "900 W Olympic Blvd /\nLos Angeles, CA 90015";
		GeocodingResponse response = client.get(address);
		assertTrue(response != null);
		assertTrue(response.getResults() != null);
		assertTrue(response.getResults().size() == 1);
		List<GeocodingResult> results = response.getResults();
		GeocodingResult result = results.get(0);
		assertTrue(result.getFormattedAddress() != null);
		assertTrue(result.getGeometry() != null);
		Geometry geometry = result.getGeometry();
		assertTrue(geometry.getLocation() != null);
		Location location = geometry.getLocation();
		assertTrue(location.getLat() != 0);
		assertTrue(location.getLng() != 0);
	}

}
