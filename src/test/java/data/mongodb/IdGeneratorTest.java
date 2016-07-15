package data.mongodb;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class IdGeneratorTest {

	@Test
	public void test() {
		String id = IdGenerator.getNewObjectId();
		assertEquals(10, id.length());
	}
	
	@Test
	public void testDuplicates() {
		Set<String> idSet = new HashSet<String>();
		for (int i = 0; i < 10000; i++) {
			String newId = IdGenerator.getNewObjectId();
			if (!idSet.contains(newId)) {
				idSet.add(newId);
//				System.out.println(newId);
			} else {
				fail(newId);
			}
		}
	}

}
