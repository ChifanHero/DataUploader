package app.bean.formatter;

import static org.junit.Assert.*;

import org.junit.Test;


public class RestaurantFormatterTest {
	
	@Test
	public void testChineseNameFormatter() {
		String mixedName = "关东123风情aaa+-；、！@#￥%……&*（）";
		String formatted = RestaurantFormatter.formatChineseName(mixedName);
		assertTrue("关东风情".equals(formatted));
	}
	
	@Test
	public void testPhoneFormat1() {
		String phone = "1234567890";
		String formatted = RestaurantFormatter.formatPhone(phone);
		assertEquals("(123) 456-7890", formatted);
	}
	
	@Test
	public void testPhoneFormat2() {
		String phone = "11234567890";
		String formatted = RestaurantFormatter.formatPhone(phone);
		assertTrue("11234567890".equals(formatted));
	}

}
