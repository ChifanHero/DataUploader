package app.bean.converter;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.junit.Test;

public class MongoDocumentConverterTest {

	@Test
	public void test1() {
		Map<String, Object> test = new HashMap<String, Object>();
		test.put("name", "韶山冲");
		test.put("address", "123 sss lane, san jose, ca, 12345");
		test.put("phone", "1234566");
		Document document = MongoDocumentConverter.convertOrCreate(test);
		assertEquals("韶山冲", document.get("name"));
		assertEquals("123 sss lane, san jose, ca, 12345", document.get("address"));
		assertEquals("1234566", document.get("phone"));
		assertNotNull(document.get("_id"));
		assertTrue(document.get("_updated_at") instanceof Date);
		assertTrue(document.get("_created_at") instanceof Date);
	}
	
	@Test
	public void test2() {
		Map<String, Object> test = new HashMap<String, Object>();
		test.put("_id", "1h2n3b4h5k");
		test.put("name", "韶山冲");
		test.put("address", "123 sss lane, san jose, ca, 12345");
		test.put("phone", "1234566");
		Date now = new Date();
		test.put("_created_at", now);
		Document document = MongoDocumentConverter.convertOrCreate(test);
		assertEquals("韶山冲", document.get("name"));
		assertEquals("123 sss lane, san jose, ca, 12345", document.get("address"));
		assertEquals("1234566", document.get("phone"));
		assertNotNull(document.get("_id"));
		assertEquals(document.get("_created_at"), now);
		assertEquals(document.get("_id"), "1h2n3b4h5k");
	}

}
