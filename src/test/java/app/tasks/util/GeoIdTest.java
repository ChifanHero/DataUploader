package app.tasks.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class GeoIdTest {

	@Test
	public void test() {
		String geoId = GeoId.from(37.123445756565, -121.334566777);
		assertEquals("37.1234|-121.3345", geoId);
	}

}
