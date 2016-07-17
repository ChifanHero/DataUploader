package app.logger;

import org.junit.Test;

public class StatusLoggerTest {
	
	StatusLogger logger = StatusLogger.getInstance();

	@Test
	public void test() {
		logger.summaryLogger.logSkippedRestaurant("韶山冲", "failed to get address info");
		logger.summaryLogger.logSkippedRestaurant("巴蜀风", "failed to get address info");
		logger.summaryLogger.logTotalRecordsFromDB(1000);
		logger.summaryLogger.logTotalRows(800);
		logger.mongoLogger.setSuccess(false);
		logger.mongoLogger.addError("Exception happened");
		logger.geoCodingLogger.logFailReason("1234 Hamilton Ave, San Jose, CA, 12345", "Didn't get info from google");
		logger.geoCodingLogger.logFailReason("2134 Leigh Ave", "Ambigous address");
		logger.print();
	}

}
