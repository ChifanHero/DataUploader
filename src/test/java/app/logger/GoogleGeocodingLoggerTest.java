package app.logger;

import org.junit.Test;

public class GoogleGeocodingLoggerTest {
	
	GoogleGeocodingLogger logger = StatusLogger.getInstance().geoCodingLogger;

	@Test
	public void test() {
		logger.logFailReason("1234 Hamilton Ave, San Jose, CA, 12345", "Didn't get info from google");
		logger.logFailReason("2134 Leigh Ave", "Ambigous address");
		logger.print();
	}

}
