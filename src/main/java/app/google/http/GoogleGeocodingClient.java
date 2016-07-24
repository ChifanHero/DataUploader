package app.google.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.map.ObjectMapper;

import app.google.http.response.GeocodingResponse;

public class GoogleGeocodingClient {
	
	private String apiKey;
	private final static String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
	private ObjectMapper mapper = new ObjectMapper();
	
	public GoogleGeocodingClient(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public GeocodingResponse get(String address) throws ClientProtocolException, IOException {
		if (address == null) {
			return null;
		}
//		address = address.trim().replaceAll(" ", "+").replaceAll("\n", "+").replaceAll("\r", "+");
		address = address.trim().replaceAll("\n", "+").replaceAll("\r", "+");
		address = URLEncoder.encode(address, "UTF-8");
		String url = BASE_URL + "address=" + address + "&key=" + apiKey;
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);
		
		BufferedReader rd = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		if (response.getStatusLine().getStatusCode() != 200) {
			System.err.println("failed to call geocoding api. Response code is " + response.getStatusLine().getStatusCode());
			System.out.println(result.toString());
			return null;
		}
		GeocodingResponse geocodingResponse = mapper.readValue(result.toString(), GeocodingResponse.class);
		return geocodingResponse;
	}


}
