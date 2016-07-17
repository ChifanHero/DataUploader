package app.tasks.util;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class RegexUtilTest {

	@Test
	public void test() {
		String s = "sentance: \"This part should be found\", \"this part too\"";
		List<String> matched =RegexUtil.getSubstringBetweenQuotes(s);
		assertEquals(2, matched.size());
		assertTrue(matched.contains("This part should be found"));
		assertTrue(matched.contains("this part too"));
	}

}
