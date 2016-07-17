package app.csv;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class MapCSVParserTest {

	@Test
	public void test() {
		String headerLine = "name,address,phone, with space ";
		MapCSVParser parser = new MapCSVParser(headerLine);
		Map<Integer, String> headers = parser.getHeaders();
		assertEquals("name", headers.get(0));
		assertEquals("address", headers.get(1));
		assertEquals("phone", headers.get(2));
		assertEquals("with space", headers.get(3));
	}
	
	@Test
	public void testGetLine() {
		String line = "川香园 / Szechuan Garden,\"4309 198th St SW, Lynnwood, WA 98036\",425) 672-6383,,";
		String headerLine = "name,address,phone,,";
		MapCSVParser parser = new MapCSVParser(headerLine);
		Map<String, Object> result= parser.parseLine(line);
		assertEquals("川香园", result.get("name"));
		assertEquals("4309 198th St SW, Lynnwood, WA 98036", result.get("address"));
		assertEquals("(425) 672-6383", result.get("phone"));
		assertEquals(3, result.size());
	}
	
	@Test
	public void testGetLine2() {
		String line = "川香园 / Szechuan Garden,4309 198th St SW,425) 672-6383,,";
		String headerLine = "name,address,phone,,";
		MapCSVParser parser = new MapCSVParser(headerLine);
		Map<String, Object> result= parser.parseLine(line);
		assertEquals("川香园", result.get("name"));
		assertEquals("4309 198th St SW", result.get("address"));
		assertEquals("(425) 672-6383", result.get("phone"));
		assertEquals(3, result.size());
	}
	
	@Test
	public void testGetLine3() {
		String line = "川香园 / Szechuan Garden,\"4309 198th St SW, Lynnwood, WA 98036\",425) 672-6383,to_ignore,,";
		String headerLine = "name,address,phone,to_ignore,,";
		MapCSVParser parser = new MapCSVParser(headerLine);
		Map<String, Object> result= parser.parseLine(line);
		assertEquals("川香园", result.get("name"));
		assertEquals("4309 198th St SW, Lynnwood, WA 98036", result.get("address"));
		assertEquals("(425) 672-6383", result.get("phone"));
		assertEquals(3, result.size());
	}
	
	@Test
	public void testGetLine4() {
		String line = "川香园 / Szechuan Garden,\"4309 198th St SW, Lynnwood, WA 98036\",\"425, 672-6383\",to_ignore,,";
		String headerLine = "name,address,phone,to_ignore,,";
		MapCSVParser parser = new MapCSVParser(headerLine);
		Map<String, Object> result= parser.parseLine(line);
		assertEquals("川香园", result.get("name"));
		assertEquals("4309 198th St SW, Lynnwood, WA 98036", result.get("address"));
		assertEquals("(425) 672-6383", result.get("phone"));
		assertEquals(3, result.size());
	}

}
