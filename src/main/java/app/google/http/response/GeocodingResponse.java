package app.google.http.response;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class GeocodingResponse {
	
	private List<GeocodingResult> results;

	public List<GeocodingResult> getResults() {
		return results;
	}

	public void setResults(List<GeocodingResult> results) {
		this.results = results;
	}

}
