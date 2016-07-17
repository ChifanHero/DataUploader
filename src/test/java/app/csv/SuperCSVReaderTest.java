package app.csv;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SuperCSVReaderTest {

	@Test
	public void test() {
		URL url = this.getClass().getResource("/csvReader/csv_test.csv");
		try {
			List<Map<String, Object>> results = SuperCSVReader.read(url.getPath());
			assertEquals(1000, results.size());
			for (Map<String, Object> result : results) {
				assertEquals(3, result.size());
				Set<String> keySet = result.keySet();
				assertTrue(keySet.contains("english_name"));
				assertTrue(keySet.contains("address"));
				assertTrue(keySet.contains("phone"));
			}
		} catch (IOException e) {
			fail("Exception occured");
		}
	}

}
